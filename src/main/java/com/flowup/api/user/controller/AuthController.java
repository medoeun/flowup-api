package com.flowup.api.user.controller;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowup.api.common.exception.TokenRefreshException;
import com.flowup.api.common.response.BaseApiResponse;
import com.flowup.api.security.dto.JwtResponseDTO;
import com.flowup.api.security.dto.TokenRefreshRequestDTO;
import com.flowup.api.security.jwt.CustomUserDetailsService;
import com.flowup.api.security.jwt.JwtManager;
import com.flowup.api.user.dto.LoginRequest;
import com.flowup.api.security.entity.RefreshToken;
import com.flowup.api.security.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtManager jwtManager;
	private final RefreshTokenService refreshTokenService;
	private final CustomUserDetailsService customUserDetailsService;

	@PostMapping("/login")
	public BaseApiResponse<JwtResponseDTO> login(@RequestBody LoginRequest loginRequest) {
		log.info("로그인 시도: {}", loginRequest.getUsername());

		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				loginRequest.getUsername(), loginRequest.getPassword()
			)
		);
		log.info("AuthenticationManager 호출 후 - 인증 성공");

		// Access Token 생성
		String accessToken = jwtManager.generateToken(authentication);
		log.info("JWT 생성: {}", accessToken);

		// Refresh Token 생성 및 저장
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(authentication.getName());

		log.info("로그인 성공: {}", loginRequest.getUsername());
		JwtResponseDTO jwtRes = new JwtResponseDTO(accessToken, refreshToken.getToken());
		return BaseApiResponse.success("로그인 성공", jwtRes);
	}
	@PostMapping("/refresh-token")
	public ResponseEntity<BaseApiResponse<JwtResponseDTO>> refreshToken(@RequestBody TokenRefreshRequestDTO request) {
		String refreshToken = request.getRefreshToken();

		return refreshTokenService.findByToken(refreshToken)
			.map(refreshTokenService::verifyExpiration)  // 만료 확인
			.map(RefreshToken::getUser)
			.map(user -> {
				Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, Collections.emptyList());
				String newAccessToken = jwtManager.generateToken(authentication);  // 새로운 Access Token 생성

				return ResponseEntity.ok(BaseApiResponse.success("Access Token 갱신 성공", new JwtResponseDTO(newAccessToken, refreshToken)));
			})
			.orElseThrow(() -> new TokenRefreshException("유효하지 않은 Refresh Token 입니다."));
	}
}