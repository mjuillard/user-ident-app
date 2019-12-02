package com.matjuillard.useridentapp.security;

import com.matjuillard.useridentapp.SpringApplicationContext;
import com.matjuillard.useridentapp.shared.AppProperties;

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
