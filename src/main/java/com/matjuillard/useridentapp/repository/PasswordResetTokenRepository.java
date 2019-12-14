package com.matjuillard.useridentapp.repository;

import org.springframework.data.repository.CrudRepository;

import com.matjuillard.useridentapp.model.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {

	public PasswordResetTokenEntity findByToken(String token);
}
