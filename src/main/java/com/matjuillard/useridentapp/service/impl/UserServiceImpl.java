package com.matjuillard.useridentapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.matjuillard.useridentapp.exception.ErrorMessages;
import com.matjuillard.useridentapp.exception.UserServiceException;
import com.matjuillard.useridentapp.model.dto.AddressDto;
import com.matjuillard.useridentapp.model.dto.UserDto;
import com.matjuillard.useridentapp.model.entity.PasswordResetTokenEntity;
import com.matjuillard.useridentapp.model.entity.UserEntity;
import com.matjuillard.useridentapp.repository.PasswordResetTokenRepository;
import com.matjuillard.useridentapp.repository.UserRepository;
import com.matjuillard.useridentapp.service.UserService;
import com.matjuillard.useridentapp.shared.AppUtils;

@Service
public class UserServiceImpl implements UserService {

	@Value("${app-token.expirationTime}")
	private long tokenExpirationTime;

	@Value("${app-token.password-reset-expiration-time}")
	private long tokenPasswordReserExpirationTime;

	@Autowired
	UserRepository userRepository;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	SimpleEmailServiceImpl emailService;
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	AppUtils appUtils;

	@Override
	public UserDto createUser(UserDto user) {

		validateUser(user);

		if (userRepository.findByEmail(user.getEmail()) != null) {
			throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage() + " - User with email:"
					+ user.getEmail() + " exists.");
		}

		String publicUserId = appUtils.generateRandomUserId(AppUtils.RANDOM_LENGTH);
		user.setUserId(publicUserId);
		user.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setEmailVerificationToken(appUtils.generateEmailToken(publicUserId, tokenExpirationTime));
		user.setEmailVerificationStatus(Boolean.FALSE); // put to false for enable EmailVerificationSystem

		if (!CollectionUtils.isEmpty(user.getAddresses())) {
			for (AddressDto addressDto : user.getAddresses()) {
				addressDto.setUserDetails(user);
				addressDto.setAddressId(appUtils.generateRandomAddressId(AppUtils.RANDOM_LENGTH));
			}
		}

		final ModelMapper mapper = new ModelMapper();
		final UserEntity userEntity = mapper.map(user, UserEntity.class);

		UserEntity storedUserDetails = userRepository.save(userEntity);

		final UserDto createdUserDto = mapper.map(storedUserDetails, UserDto.class);

		// Email verification sender
		emailService.verifyEmail(createdUserDto);

		return createdUserDto;
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null) {
			throw new UsernameNotFoundException(email);
		}

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userEntity, userDto);
		return userDto;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserDto userDto = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + " - " + userId);
		}

		BeanUtils.copyProperties(userEntity, userDto);
		return userDto;
	}

	@Override
	public UserDto updateUser(String userId, UserDto user) {

		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + " - " + userId);
		}

		UserDto userDto = new UserDto();
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		userRepository.save(userEntity);

		BeanUtils.copyProperties(userEntity, userDto);

		return userDto;
	}

	@Override
	public void deleteUser(String userId) {

		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}

		userRepository.delete(userEntity);
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> result = new ArrayList<UserDto>();

		if (page > 0) {
			page = page - 1;
		}
		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
		List<UserEntity> userEntities = usersPage.getContent();

		for (UserEntity userEntitiy : userEntities) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntitiy, userDto);
			result.add(userDto);
		}

		return result;
	}

	// Inherited from Spring Security to find user details
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null) {
			throw new UsernameNotFoundException(email);
		}

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
				userEntity.getEmailVerificationStatus(), true, true, true, new ArrayList<GrantedAuthority>());
	}

	public void validateUser(UserDto user) {

		if (user.getEmail().isEmpty()) {
			throw new UserServiceException(
					ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage() + " - Email required.");
		}

		if (user.getPassword().isEmpty()) {
			throw new UserServiceException(
					ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage() + " - Password required.");
		}
	}

	@Override
	public boolean verifyEmailToken(String token) {

		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

		if (userEntity != null) {
			boolean hasExpired = appUtils.hasTokenExpired(token);
			if (!hasExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean requestResetPassword(String email) {

		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null) {
			return false;
		}

		String token = appUtils.generateEmailToken(userEntity.getUserId(), tokenPasswordReserExpirationTime);
		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUser(userEntity);
		passwordResetTokenRepository.save(passwordResetTokenEntity);

		return emailService.sendPasswordResetRequest(userEntity.getFirstName(), userEntity.getEmail(), token);
	}

	@Override
	public boolean resetPassword(String token, String password) {

		if (appUtils.hasTokenExpired(token)) {
			return false;
		}

		PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);
		if (passwordResetTokenEntity == null) {
			return false;
		}

		String encryptedPassword = bCryptPasswordEncoder.encode(password);

		UserEntity userEntity = passwordResetTokenEntity.getUser();
		userEntity.setEncryptedPassword(encryptedPassword);
		UserEntity savedUser = userRepository.save(userEntity);

		if (savedUser != null && encryptedPassword.equalsIgnoreCase(savedUser.getEncryptedPassword())) {
			return true;
		}

		passwordResetTokenRepository.delete(passwordResetTokenEntity);

		return false;
	}

}
