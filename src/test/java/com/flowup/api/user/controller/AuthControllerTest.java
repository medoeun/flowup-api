package com.flowup.api.user.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import com.flowup.api.common.enums.Authority;
import com.flowup.api.security.entity.RefreshToken;
import com.flowup.api.security.jwt.CustomUserDetails;
import com.flowup.api.security.jwt.CustomUserDetailsService;
import com.flowup.api.security.jwt.JwtManager;
import com.flowup.api.security.service.RefreshTokenService;
import com.flowup.api.user.entity.User;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private JwtManager jwtManager;

	@MockBean
	private RefreshTokenService refreshTokenService;

	@MockBean
	private UserDetailsService userDetailsService;

	@MockBean
	private CustomUserDetailsService customUserDetailsService; // 필요 시 추가

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	// 로그인 성공 시 Ok Status 리턴
	@Test
	public void testLogin() throws Exception {
		// given
		String username = "testuser";
		String password = "password";
		String accessToken = "test-jwt-token";
		String refreshToken = "test-refresh-token";

		User user = User.builder()
			.id(1L)
			.username(username)
			.password(password)
			.authority(Authority.USER)
			.build();

		// Authentication 객체를 모킹 (사용자가 입력한 자격 증명을 기반으로 생성된 Authentication 객체)
		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.thenReturn(authentication);

		// JWT 토큰 생성 로직을 모킹 (인증 성공 후 생성된 JWT 토큰 반환)
		when(jwtManager.generateToken(authentication)).thenReturn(accessToken);

		// 리프레시 토큰 생성 로직을 모킹 (사용자에게 할당된 리프레시 토큰 반환)
		RefreshToken refreshTokenEntity = RefreshToken.builder()
			.token(refreshToken)
			.user(user)
			.expiryDate(LocalDateTime.now().plusDays(7))  // 예시로 7일 후 만료
			.build();

		when(refreshTokenService.createRefreshToken(username)).thenReturn(refreshTokenEntity);

		// when, then : 실제 로직 실행
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"testuser\", \"password\":\"password\"}")) // 요청 본문에 JSON 형태의 사용자 자격 증명 포함
			.andExpect(status().isOk());
	}

	// 리프레시 토큰이 유효할 시 새로운 액세스 토큰 반환
	@Test
	public void testRefreshToken() throws Exception {
		// given
		String refreshToken = "test-refresh-token";
		String newAccessToken = "new-jwt-token";

		User user = User.builder()
			.id(1L)
			.username("testuser")
			.password("password")
			.authority(Authority.USER)
			.build();

		RefreshToken validRefreshToken = RefreshToken.builder()
			.token(refreshToken)
			.user(user)
			.expiryDate(LocalDateTime.now().plusDays(7))  // 예시로 7일 후 만료
			.build();

		UserDetails userDetails = new CustomUserDetails(user);

		when(refreshTokenService.findByToken(refreshToken)).thenReturn(Optional.of(validRefreshToken));
		when(refreshTokenService.verifyExpiration(validRefreshToken)).thenReturn(validRefreshToken);
		when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(userDetails);
		when(jwtManager.generateToken(any(Authentication.class))).thenReturn(newAccessToken);

		// when, then
		mockMvc.perform(post("/api/auth/refresh-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"refreshToken\":\"test-refresh-token\"}"))
			.andExpect(status().isOk());
	}

}

