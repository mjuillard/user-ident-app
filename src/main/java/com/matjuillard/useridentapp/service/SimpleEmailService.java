package com.matjuillard.useridentapp.service;

import com.matjuillard.useridentapp.model.dto.UserDto;

public interface SimpleEmailService {

	public void verifyEmail(UserDto userDto);

}
