package com.flowup.api.security.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

	@Bean(name = "testSecurityFilterChain")
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // CSRF 보호를 비활성화
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/api/auth/**").permitAll()  // /api/auth/** 경로에 대한 인증 비활성화
				.anyRequest().authenticated()  // 그 외의 모든 요청 인증 필요
			);

		return http.build();
	}

	@Bean(name = "testUserDetailsService")
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withUsername("testuser")
			.password(passwordEncoder().encode("password"))
			.authorities("USER")
			.build();

		return new InMemoryUserDetailsManager(user);  // 인메모리 사용자 저장소 설정
	}

	@Bean(name = "testPasswordEncoder")
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();  // 비밀번호 암호화를 위해 BCryptPasswordEncoder 사용
	}
}