package com.matjuillard.user.ident.server.security;

import com.matjuillard.user.ident.server.SpringApplicationContext;
import com.matjuillard.user.ident.server.shared.AppProperties;

public class SecurityConstants {

	private static AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");

	public static final String HEADER_STRING = "Authorization";

	public static String getTokenPrefix() {
		return appProperties.getTokenPrefix();
	}

	public static String getTokenSecret() {
		return appProperties.getTokenSecret();
	}

	public static long getTokenExpirationTime() {
		return appProperties.getTokenExpirationTime();
	}
}
