package com.matjuillard.user.ident.server.shared;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.matjuillard.user.ident.server.shared.AppUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AppUtilsTest {

	private final static int TEST_LENGTH = 48;
	private final static int TEST_TOKEN_EXPIRATION = 3600;
	private final static String USER_ID_TEST = "testUserId";

	@Autowired
	AppUtils appUtils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateRandomUserId() {

		String userId = appUtils.generateRandomUserId(TEST_LENGTH);
		String userId2 = appUtils.generateRandomUserId(TEST_LENGTH);

		assertNotNull(userId);
		assertNotNull(userId2);
		assertTrue(userId.length() == TEST_LENGTH);
		assertTrue(!userId.equalsIgnoreCase(userId2));
	}

	@Test
	// @Disabled
	void testHasTokenNonExpired() {

		String token = appUtils.generateEmailToken(USER_ID_TEST, TEST_TOKEN_EXPIRATION);
		assertNotNull(token);

		boolean hasTokenExpired = appUtils.hasTokenExpired(token);
		assertFalse(hasTokenExpired);
	}

	@Test
	void testHasTokenExpired() {

		String token = appUtils.generateEmailToken(USER_ID_TEST, 0);
		assertNotNull(token);

		boolean hasTokenExpired = appUtils.hasTokenExpired(token);
		assertTrue(hasTokenExpired);
	}

}
