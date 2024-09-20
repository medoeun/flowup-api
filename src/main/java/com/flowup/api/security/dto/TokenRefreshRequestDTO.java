package com.flowup.api.security.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenRefreshRequestDTO {
	private String refreshToken;  // 클라이언트에서 보내는 Refresh Token
}
