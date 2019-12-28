package com.matjuillard.user.ident.server.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.matjuillard.user.ident.server.model.entity.UserEntity;
import com.matjuillard.user.ident.server.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

	private static final String USER_ID_TEST_VALUE = "anyUserId";
	private static final String ENCRYPTED_PASSWORD_TEST_VALUE = "encryptedPassword";
	private static final String EMAIL_TEST_VALUE = "test@test.com";

	@Autowired
	UserRepository userRepository;

	@BeforeEach
	void setUp() throws Exception {

		UserEntity userEntity = new UserEntity();
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setUserId(USER_ID_TEST_VALUE);
		userEntity.setFirstName("user1FirstName");
		userEntity.setLastName("user1LastName");
		userEntity.setEncryptedPassword(ENCRYPTED_PASSWORD_TEST_VALUE);
		userEntity.setEmail(EMAIL_TEST_VALUE);
		userEntity.setEmailVerificationToken("emailVerifToken");
		userEntity.setEmailVerificationStatus(true);
		userRepository.save(userEntity);
	}

	@Test
	void testGetVerifedUsers() {
		Pageable pageableRequest = PageRequest.of(0, 2);
		Page<UserEntity> page = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		List<UserEntity> users = page.getContent();
		assertNotNull(users);
	}

}
