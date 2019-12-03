package com.matjuillard.useridentapp.service.impl;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.matjuillard.useridentapp.exception.ErrorMessages;
import com.matjuillard.useridentapp.exception.UserServiceException;
import com.matjuillard.useridentapp.model.dto.UserDto;
import com.matjuillard.useridentapp.model.entity.UserEntity;
import com.matjuillard.useridentapp.repository.UserRepository;
import com.matjuillard.useridentapp.service.UserService;
import com.matjuillard.useridentapp.shared.AppUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	AppUtils appUtils;

	@Override
	public UserDto createUser(UserDto user) {

		validateUser(user);

		if (userRepository.findByEmail(user.getEmail()) != null) {
			throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage() + " - User with email:"
					+ user.getEmail() + " exists.");
		}

		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);

		String publicUserId = appUtils.generateRandomUserId(AppUtils.RANDOM_LENGTH);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		UserEntity storedUserDetails = userRepository.save(userEntity);
		UserDto createdUserDto = new UserDto();
		BeanUtils.copyProperties(storedUserDetails, createdUserDto);

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

	// Inherited from Spring Security to find user details
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null) {
			throw new UsernameNotFoundException(email);
		}

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<GrantedAuthority>());
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
}
