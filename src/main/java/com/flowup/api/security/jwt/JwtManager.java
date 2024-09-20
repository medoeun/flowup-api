package com.flowup.api.security.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtManager {

	private final Key secretKey;
	private final long tokenValidityInseconds;

	public JwtManager(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.token-validity-in-seconds}") long tokenValidityInseconds) {
		byte[] keyBytes = Base64.getDecoder().decode(secret);
		this.secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
		this.tokenValidityInseconds = tokenValidityInseconds;
	}

	// JWT 토큰 생성
	public String generateToken(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		String username = userDetails.getUsername();

		// JWT 토큰에 최소한의 정보(페이로드)만 포함
		Claims claims = Jwts.claims().setSubject(username);

		Date now = new Date();
		Date validity = new Date(now.getTime() + tokenValidityInseconds * 1000);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	// HTTP 요청 헤더에서 JWT 토큰을 추출
	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);  // "Bearer " 이후의 토큰 실제부분만 추출
		}
		return null;
	}

	public Authentication getAuthentication(String token, UserDetails userDetails) {
		return new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());
	}

	public String getUsername(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}

	// JWT 토큰 유효성 검증
	public boolean isValidToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token: {}", token);
			return false;
		} catch (JwtException | IllegalArgumentException e) {
			log.info("Invalid JWT token: {}", token);
			log.info("Exception message: {}", e.getMessage());
			return false;
		}
	}
}
