package com.flowup.api.common.exception;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowup.api.common.response.BaseApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		// HTTP 응답 설정
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		// BaseApiResponse 형식으로 예외 응답 작성
		BaseApiResponse<String> apiResponse = BaseApiResponse.failure("인증이 필요합니다.");

		// JSON으로 응답 전송
		response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
	}
}
