package com.matjuillard.useridentapp.shared;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

	@Value("${token.prefix}")
	private String tokenPrefix;

	@Value("${token.secret}")
	private String tokenSecret;

	@Value("${token.expirationTime}")
	private long tokenExpirationTime;

	public String getTokenPrefix() {
		return tokenPrefix;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public long getTokenExpirationTime() {
		return tokenExpirationTime;
	}
}
