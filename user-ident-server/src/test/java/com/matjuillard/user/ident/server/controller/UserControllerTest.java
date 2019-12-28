package com.matjuillard.user.ident.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.matjuillard.user.ident.server.controller.UserController;
import com.matjuillard.user.ident.server.model.dto.AddressDto;
import com.matjuillard.user.ident.server.model.dto.UserDto;
import com.matjuillard.user.ident.server.model.response.UserRest;
import com.matjuillard.user.ident.server.service.AddressService;
import com.matjuillard.user.ident.server.service.UserService;

class UserControllerTest {

	private static final String USER_ID_TEST_VALUE = "anyUserId";
	private static final String PASSWORD_TEST_VALUE = "encryptedPassword";
	private static final String EMAIL_TEST_VALUE = "test@test.com";

	@InjectMocks
	UserController userController;

	@Mock
	private UserService userService;
	@Mock
	private AddressService addressService;

	UserDto userDto;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		userDto = new UserDto();
		userDto.setUserId(USER_ID_TEST_VALUE);
		userDto.setAddresses(getAddressDtoList());
		userDto.setFirstName("firstNameTest");
		userDto.setLastName("lastNameTest");
		userDto.setEmail(EMAIL_TEST_VALUE);
		userDto.setPassword(PASSWORD_TEST_VALUE);
	}

	@Test
	void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);

		UserRest userRest = userController.getUser(USER_ID_TEST_VALUE);
		assertNotNull(userRest);
		assertEquals(USER_ID_TEST_VALUE, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
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

}
