package com.matjuillard.useridentapp.shared;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

	@Value("${app-token.prefix}")
	private String tokenPrefix;

	@Value("${app-token.secret}")
	private String tokenSecret;

	@Value("${app-token.expirationTime}")
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
