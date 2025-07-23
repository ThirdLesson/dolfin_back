package org.scoula.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JwtToken {
	private String grantType;
	private String accessToken;
	private String refreshToken;
}
