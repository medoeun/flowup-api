package com.flowup.api.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowup.api.security.entity.RefreshToken;
import com.flowup.api.user.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);
	void deleteByUser(User user);
}
