package com.flowup.api.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flowup.api.common.enums.Authority;
import com.flowup.api.user.entity.User;

@SpringBootTest
public class JwtManagerTest {

	@Mock
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtManager jwtManager;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testGenerateAndValidateToken() {
		// given
		// User 엔티티 생성
		User user = User.builder()
			.id(1L)
			.username("testuser")
			.password("password")
			.authority(Authority.USER)
			.build();

		// UserDetails 생성
		UserDetails userDetails = new CustomUserDetails(user);

		// Mock 설정
		when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

		// Authentication 객체 생성
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities()
		);

		// when
		String token = jwtManager.generateToken(authentication); // Authentication 객체를 사용

		// then
		assertTrue(jwtManager.isValidToken(token));
		assertEquals("testuser", jwtManager.getUsername(token));
	}
}
