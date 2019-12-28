package com.matjuillard.user.ident.server.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.matjuillard.user.ident.server.model.dto.UserDto;

public interface UserService extends UserDetailsService {

	UserDto createUser(UserDto user);

	UserDto getUser(String email);

	UserDto getUserByUserId(String userId);

	UserDto updateUser(String userId, UserDto user);

	void deleteUser(String userId);

	List<UserDto> getUsers(int page, int limit);

	boolean verifyEmailToken(String token);

	boolean requestResetPassword(String email);

	boolean resetPassword(String token, String password);

	/**
	 * Method to bypass sending E-mail management - Should not be there in real
	 * Life!
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	boolean requestResetDirectPassword(String email, String password);
}
