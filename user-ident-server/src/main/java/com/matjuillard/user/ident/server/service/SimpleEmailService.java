package com.matjuillard.user.ident.server.service;

import com.matjuillard.user.ident.server.model.dto.UserDto;

public interface SimpleEmailService {

	public void verifyEmail(UserDto userDto);

}
