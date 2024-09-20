package com.flowup.api.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
	private String accessToken;
	private String refreshToken;
}
