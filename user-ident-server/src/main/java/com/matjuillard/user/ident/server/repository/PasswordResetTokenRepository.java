package com.matjuillard.user.ident.server.repository;

import org.springframework.data.repository.CrudRepository;

import com.matjuillard.user.ident.server.model.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {

	public PasswordResetTokenEntity findByToken(String token);
}
