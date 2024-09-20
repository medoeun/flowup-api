package com.flowup.api.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtManager jwtManager;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		// Swagger UI 및 관련 요청은 필터링하지 않음
		String path = request.getRequestURI();
		if (path.startsWith("/swagger-ui") || path.startsWith("/webjars/") || path.startsWith("/v3/api-docs")) {
			filterChain.doFilter(request, response);
			return;
		}

		// SecurityContext에 이미 인증 정보가 있는지 확인
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			// request에서 토큰 파싱
			String token = jwtManager.resolveToken(request);

			// 토큰이 유효한 경우에만 인증 처리
			if (token != null && jwtManager.isValidToken(token)) {
				Long userId = jwtManager.getUserId(token);

				// UserDetailsService를 통해 UserDetails를 로드
				var userDetails = userDetailsService.loadUserByUsername(userId.toString());

				// Authentication 생성 및 설정
				Authentication authentication = jwtManager.getAuthentication(token, userDetails);

				// Authentication이 UsernamePasswordAuthenticationToken일 경우에만 setDetails 호출
				if (authentication instanceof UsernamePasswordAuthenticationToken) {
					((UsernamePasswordAuthenticationToken) authentication)
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				}
				// authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// SecurityContext에 인증 정보 설정
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		// 다음 필터로 요청 전달
		filterChain.doFilter(request, response);
	}
}
