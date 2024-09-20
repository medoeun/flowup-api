package com.flowup.api.security.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.flowup.api.common.exception.TokenRefreshException;
import com.flowup.api.security.entity.RefreshToken;
import com.flowup.api.user.entity.User;
import com.flowup.api.security.repository.RefreshTokenRepository;
import com.flowup.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	@Value("${jwt.refresh-token-expiration-ms}")
	private final Long refreshTokenExpirationMs;

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	// Refresh Token 생성
	public RefreshToken createRefreshToken(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

		RefreshToken refreshToken = RefreshToken.builder()
			.user(user)
			.token(UUID.randomUUID().toString())  // 고유한 토큰 값 생성
			.expiryDate(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpirationMs)))  // 만료 시간 설정
			.build();
		return refreshTokenRepository.save(refreshToken);
	}

	// Refresh Token 만료 여부 확인
	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
			refreshTokenRepository.delete(token);  // 만료된 토큰 삭제
			throw new TokenRefreshException("Refresh token이 만료되었습니다.");
		}
		return token;
	}

	// Refresh Token 삭제
	public void deleteByUserId(Long userId) {
		refreshTokenRepository.deleteByUser(
			userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"))
		);
	}
}
