package com.flowup.api.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowup.api.common.response.BaseApiResponse;
import com.flowup.api.security.jwt.JwtManager;
import com.flowup.api.user.dto.LoginRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtManager jwtManager;
	private final UserDetailsService userDetailsService;

	@PostMapping("/login")
	public ResponseEntity<BaseApiResponse<String>> authenticateUser(@RequestBody LoginRequest loginRequest) {
		try {
			// 사용자의 아이디와 패스워드를 기반으로 인증을 시도합니다.
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					loginRequest.getUsername(),
					loginRequest.getPassword()
				)
			);

			// 인증 성공 시 SecurityContext에 설정
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// JWT 토큰 생성
			String jwt = jwtManager.generateToken(authentication);

			// 토큰을 포함한 응답 반환
			return ResponseEntity.ok(BaseApiResponse.success("로그인 성공",jwt));

		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(BaseApiResponse.failure("로그인 실패: 잘못된 사용자명 또는 비밀번호입니다."));
		}
	}
}