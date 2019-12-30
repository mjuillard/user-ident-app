package com.matjuillard.user.ident.server.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matjuillard.user.ident.server.SpringApplicationContext;
import com.matjuillard.user.ident.server.model.dto.UserDto;
import com.matjuillard.user.ident.server.model.request.UserLoginRequestModel;
import com.matjuillard.user.ident.server.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private static final String USER_ID_HEADER = "userId";
	private static final String EXPIRATION_TIME_HEADER = "expirationTime";

	private final AuthenticationManager authenticationManager;

	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		try {
			UserLoginRequestModel credentials = new ObjectMapper().readValue(request.getInputStream(),
					UserLoginRequestModel.class);
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		final String userName = ((User) authResult.getPrincipal()).getUsername();
		final Date expirationDate = new Date(System.currentTimeMillis() + SecurityConstants.getTokenExpirationTime());

		String token = Jwts.builder().setSubject(userName).setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()).compact();

		UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
		UserDto userDto = userService.getUser(userName);

		response.addHeader("Access-Control-Expose-Headers",
				SecurityConstants.HEADER_STRING + "," + USER_ID_HEADER + "," + EXPIRATION_TIME_HEADER);
		response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.getTokenPrefix() + token);
		response.addHeader(USER_ID_HEADER, userDto.getUserId());
		response.addHeader(EXPIRATION_TIME_HEADER, String.valueOf(expirationDate.getTime()));
	}

}
