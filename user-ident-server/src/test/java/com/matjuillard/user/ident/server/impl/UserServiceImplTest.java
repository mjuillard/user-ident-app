package com.matjuillard.user.ident.server.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.matjuillard.user.ident.server.exception.UserServiceException;
import com.matjuillard.user.ident.server.model.dto.AddressDto;
import com.matjuillard.user.ident.server.model.dto.UserDto;
import com.matjuillard.user.ident.server.model.entity.AddressEntity;
import com.matjuillard.user.ident.server.model.entity.UserEntity;
import com.matjuillard.user.ident.server.model.response.AddressRest;
import com.matjuillard.user.ident.server.repository.UserRepository;
import com.matjuillard.user.ident.server.service.impl.SimpleEmailServiceImpl;
import com.matjuillard.user.ident.server.service.impl.UserServiceImpl;
import com.matjuillard.user.ident.server.shared.AppUtils;

class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userService;

	@Mock
	UserRepository userRepository;
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Mock
	SimpleEmailServiceImpl emailService;
	@Mock
	AppUtils appUtils;

	private static final String USER_ID_TEST_VALUE = "anyUserId";
	private static final String PASSWORD_TEST_VALUE = "encryptedPassword";
	private static final String ENCRYPTED_PASSWORD_TEST_VALUE = "encryptedPassword";
	private static final String EMAIL_TEST_VALUE = "test@test.com";
	private UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setUserId(USER_ID_TEST_VALUE);
		userEntity.setFirstName("user1FirstName");
		userEntity.setLastName("user1LastName");
		userEntity.setEncryptedPassword(ENCRYPTED_PASSWORD_TEST_VALUE);
		userEntity.setEmail(EMAIL_TEST_VALUE);
		userEntity.setEmailVerificationToken("emailVerifToken");
		userEntity.setAddresses(getAddressEntityList());
	}

	@Test
	void testGetUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		UserDto userDto = userService.getUser(EMAIL_TEST_VALUE);

		assertNotNull(userDto);
		assertEquals(userEntity.getFirstName(), userDto.getFirstName());
		assertEquals(userEntity.getLastName(), userDto.getLastName());
	}

	@Test
	final void testGetUser_UsernameNotFoundException() {
		when(userRepository.findByEmail(anyString())).thenReturn(null);

		assertThrows(UsernameNotFoundException.class, () -> {
			userService.getUser(EMAIL_TEST_VALUE);
		});
	}

	@Test
	final void testCreateUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(appUtils.generateRandomAddressId(anyInt())).thenReturn("anyAddressId");
		when(appUtils.generateRandomUserId(anyInt())).thenReturn(USER_ID_TEST_VALUE);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(ENCRYPTED_PASSWORD_TEST_VALUE);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		// Mockito.doNothing().when(emailService).verifyEmail(any(UserDto.class));

		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressDtoList());
		userDto.setFirstName("firstNameTest");
		userDto.setLastName("lastNameTest");
		userDto.setEmail(EMAIL_TEST_VALUE);
		userDto.setPassword(PASSWORD_TEST_VALUE);

		UserDto createdUserDto = userService.createUser(userDto);

		assertNotNull(createdUserDto);
		assertEquals(userEntity.getFirstName(), createdUserDto.getFirstName());
		assertEquals(userEntity.getLastName(), createdUserDto.getLastName());
		assertNotNull(createdUserDto.getUserId());
		assertEquals(userEntity.getAddresses().size(), createdUserDto.getAddresses().size());
		// generateRandomUserId is called 1 time for given user
		verify(appUtils, times(1)).generateRandomUserId(AppUtils.RANDOM_LENGTH);
		// generateRandomAddressId is called 2 times
		verify(appUtils, times(userEntity.getAddresses().size())).generateRandomAddressId(AppUtils.RANDOM_LENGTH);
		// encrypt password is called only 1 time
		verify(bCryptPasswordEncoder, times(1)).encode(PASSWORD_TEST_VALUE);
		verify(userRepository, times(1)).save(any(UserEntity.class));
	}

	@Test
	final void testCreateUser_UserServiceException() {

		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressDtoList());
		userDto.setFirstName("firstNameTest");
		userDto.setLastName("lastNameTest");
		userDto.setEmail(EMAIL_TEST_VALUE);
		userDto.setPassword(PASSWORD_TEST_VALUE);

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		assertThrows(UserServiceException.class, () -> {
			userService.createUser(userDto);
		});
	}

	private List<AddressDto> getAddressDtoList() {

		AddressDto shippingAddressDto = new AddressDto();
		shippingAddressDto.setType("shipping");
		shippingAddressDto.setCity("cityTest");
		shippingAddressDto.setCountry("CountryTest");
		shippingAddressDto.setPostalCode("PostalCodeTest");
		shippingAddressDto.setStreetName("StreetNameTest");

		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("billing");
		billingAddressDto.setCity("cityTest");
		billingAddressDto.setCountry("CountryTest");
		billingAddressDto.setPostalCode("PostalCodeTest");
		billingAddressDto.setStreetName("StreetNameTest");

		List<AddressDto> addresses = new ArrayList<AddressDto>();
		addresses.add(shippingAddressDto);
		addresses.add(billingAddressDto);

		return addresses;
	}

	private List<AddressEntity> getAddressEntityList() {
		List<AddressDto> addresses = getAddressDtoList();

		Type listType = new TypeToken<List<AddressRest>>() {
		}.getType();
		return new ModelMapper().map(addresses, listType);
	}

}
