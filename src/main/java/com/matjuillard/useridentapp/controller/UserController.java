package com.matjuillard.useridentapp.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matjuillard.useridentapp.model.dto.AddressDto;
import com.matjuillard.useridentapp.model.dto.UserDto;
import com.matjuillard.useridentapp.model.request.PasswordResetModel;
import com.matjuillard.useridentapp.model.request.PasswordResetRequestModel;
import com.matjuillard.useridentapp.model.request.UserDetailsRequestModel;
import com.matjuillard.useridentapp.model.response.AddressRest;
import com.matjuillard.useridentapp.model.response.OperationStatusModel;
import com.matjuillard.useridentapp.model.response.RequestOperationName;
import com.matjuillard.useridentapp.model.response.RequestOperationStatus;
import com.matjuillard.useridentapp.model.response.UserRest;
import com.matjuillard.useridentapp.service.AddressService;
import com.matjuillard.useridentapp.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private static final String ADDRESS_LINK = "address";
	private static final String ADDRESSES_LINK = "addresses";
	private static final String USER_LINK = "user";

	@Autowired
	private UserService userService;
	@Autowired
	private AddressService addressService;

	private final ModelMapper mapper = new ModelMapper();

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest getUser(@PathVariable String id) {
		UserRest user = new UserRest();
		UserDto userDto = userService.getUserByUserId(id);

		BeanUtils.copyProperties(userDto, user);
		return user;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

		UserDto userDto = mapper.map(userDetails, UserDto.class);
		UserDto createdUser = userService.createUser(userDto);
		UserRest user = mapper.map(createdUser, UserRest.class);

		return user;
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

		UserRest user = new UserRest();
		UserDto userDto = new UserDto();

		BeanUtils.copyProperties(userDetails, userDto);

		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, user);

		return user;
	}

	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {

		OperationStatusModel status = new OperationStatusModel();
		status.setOperationName(RequestOperationName.DELETE.name());

		userService.deleteUser(id);

		status.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return status;
	}

	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {

		List<UserRest> users = new ArrayList<UserRest>();
		List<UserDto> userDtoList = userService.getUsers(page, limit);

		for (UserDto userDto : userDtoList) {
			UserRest user = new UserRest();
			BeanUtils.copyProperties(userDto, user);
			users.add(user);
		}

		return users;
	}

	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id) {
		List<AddressRest> result = new ArrayList<AddressRest>();
		List<AddressDto> addressesDto = addressService.getAddresses(id);

		if (!CollectionUtils.isEmpty(addressesDto)) {
			Type listType = new TypeToken<List<AddressRest>>() {
			}.getType();
			result = mapper.map(addressesDto, listType);

			// Add Hateoas links
			for (AddressRest addressRest : result) {
				Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId()))
						.withSelfRel();
				Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel(USER_LINK);
				addressRest.add(addressLink);
				addressRest.add(userLink);
			}
		}

		return new CollectionModel<AddressRest>(result);
	}

	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public EntityModel<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		AddressDto address = addressService.getAddress(addressId);

		AddressRest addressesRest = mapper.map(address, AddressRest.class);

		// Add Hateoas links
		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel(USER_LINK);
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel(ADDRESSES_LINK);
		addressesRest.add(addressLink);
		addressesRest.add(userLink);
		addressesRest.add(addressesLink);

		return new EntityModel<AddressRest>(addressesRest);
	}

	@GetMapping(path = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

		OperationStatusModel result = new OperationStatusModel();
		result.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

		if (userService.verifyEmailToken(token)) {
			result.setOperationResult(RequestOperationStatus.SUCCESS.name());
		} else {
			result.setOperationResult(RequestOperationStatus.ERROR.name());
		}

		return result;
	}

	@PostMapping(path = "/password-reset-request", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel resetPasswordRequest(@RequestBody PasswordResetRequestModel passwordResetRequestModel)
			throws Exception {

		OperationStatusModel result = new OperationStatusModel();

		boolean operationResult = userService.requestResetPassword(passwordResetRequestModel.getEmail());
		result.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		result.setOperationResult(RequestOperationStatus.ERROR.name());

		if (operationResult) {
			result.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}

		return result;
	}

	@PostMapping(path = "/password-reset", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) throws Exception {

		OperationStatusModel result = new OperationStatusModel();
		boolean operationResult = userService.resetPassword(passwordResetModel.getToken(),
				passwordResetModel.getPassword());

		result.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		result.setOperationResult(RequestOperationStatus.ERROR.name());

		if (operationResult) {
			result.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}

		return result;
	}

	@PostMapping(path = "/password-reset-request-direct", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel resetPasswordRequest(@RequestBody PasswordResetRequestModel passwordResetRequestModel,
			@RequestBody String email, @RequestBody String newPassword) throws Exception {

		OperationStatusModel result = new OperationStatusModel();

		boolean operationResult = userService.requestResetDirectPassword(email, newPassword);
		result.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		result.setOperationResult(RequestOperationStatus.ERROR.name());

		if (operationResult) {
			result.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}

		return result;
	}
}
