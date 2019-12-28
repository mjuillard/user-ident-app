package com.matjuillard.user.ident.server.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.matjuillard.user.ident.server.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class AppUtils {

	public final static int RANDOM_LENGTH = 48;
	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public String generateRandomUserId(int length) {
		return generateRandomString(length);
	}

	public String generateRandomAddressId(int length) {
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {
		StringBuilder returnValue = new StringBuilder();

		for (int i = 0; i < length; i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}

		return returnValue.toString();
	}

	public boolean hasTokenExpired(String token) {

		try {

			Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token)
					.getBody();
			Date tokenExpirationDate = claims.getExpiration();
			Date currentDate = new Date();

			return tokenExpirationDate.before(currentDate);

		} catch (ExpiredJwtException e) {

			return true;
		}
	}

	public String generateEmailToken(String userId, long tokenExpirationTime) {

		String token = Jwts.builder().setSubject(userId)
				.setExpiration(new Date(System.currentTimeMillis() + tokenExpirationTime))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()).compact();

		return token;
	}
}
