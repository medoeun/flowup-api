package com.flowup.api.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;

import com.flowup.api.common.enums.Authority;
import com.flowup.api.user.entity.User;

@ExtendWith(MockitoExtension.class)
public class JwtManagerTest {

	@Mock
	private UserDetailsService userDetailsService;

	private JwtManager jwtManager;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		jwtManager = new JwtManager("NGM2dmtVcVltd0lwN0xJdG5kS29tc2lVaDJUNDd6V20=", 3600L); // 직접 인스턴스 생성
	}

	@Test
	public void testSecretKey() {
		assertNotNull(jwtManager,"JwtManager should not be null");
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
