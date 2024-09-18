package com.flowup.api.common.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
	private static final String BEARER_SCHEME = "bearer";
	private static String SECURITY_SCHEME_NAME = "JWT";

	@Bean
	public OpenAPI openAPI() {
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(SECURITY_SCHEME_NAME);
		return new OpenAPI()
			.addServersItem(new Server().url("/").description("현재 서버"))
			.components(apiComponents())
			.addSecurityItem(securityRequirement)
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("FlowUp API Docs")
			.description("칸반보드 기반 프로젝트 관리 및 분석 시스템")
			.version("1.0");
	}

	private Components apiComponents() {
		return new Components()
			.addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
				.name(SECURITY_SCHEME_NAME)
				.type(SecurityScheme.Type.HTTP)
				.scheme(BEARER_SCHEME)
				.bearerFormat(SECURITY_SCHEME_NAME));
	}
}