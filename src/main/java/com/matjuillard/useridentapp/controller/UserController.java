package com.matjuillard.useridentapp.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

		// UserDto userDto = new UserDto();
		// BeanUtils.copyProperties(userDetails, userDto);
		// ModelMapper mapper = new ModelMapper();
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
			MediaType.APPLICATION_XML_VALUE })
	public List<AddressRest> getAddresses(@PathVariable String id) {
		List<AddressRest> result = new ArrayList<AddressRest>();
		List<AddressDto> addressesDto = addressService.getAddresses(id);

		if (!CollectionUtils.isEmpty(addressesDto)) {
			Type listType = new TypeToken<List<AddressRest>>() {
			}.getType();
			result = mapper.map(addressesDto, listType);
		}

		return result;
	}

	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public AddressRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		AddressDto address = addressService.getAddress(addressId);
		return mapper.map(address, AddressRest.class);
	}
}
