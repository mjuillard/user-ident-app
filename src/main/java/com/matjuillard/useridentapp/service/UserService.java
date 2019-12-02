package com.matjuillard.useridentapp.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.matjuillard.useridentapp.model.dto.UserDto;

public interface UserService extends UserDetailsService {

	UserDto createUser(UserDto user);

	UserDto getUser(String email);
}
