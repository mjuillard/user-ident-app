package com.matjuillard.useridentapp.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matjuillard.useridentapp.model.dto.UserDto;
import com.matjuillard.useridentapp.model.request.UserDetailsRequestModel;
import com.matjuillard.useridentapp.model.response.UserRest;
import com.matjuillard.useridentapp.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping()
	public String getUser() {
		return "get one user ";
	}

	@PostMapping()
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
		UserRest user = new UserRest();
		UserDto userDto = new UserDto();

		BeanUtils.copyProperties(userDetails, userDto);

		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, user);

		return user;
	}

	@PutMapping()
	public String updateUser() {
		return "Update user";
	}

	@DeleteMapping()
	public String deleteUser() {
		return "Delete user";
	}
}
