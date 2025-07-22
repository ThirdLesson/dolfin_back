package org.scoula.domain.codef.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CodefTokenResponse(
	String accessToken,
	String tokenType,
	Integer expiresIn,
	String scope
) {
}
