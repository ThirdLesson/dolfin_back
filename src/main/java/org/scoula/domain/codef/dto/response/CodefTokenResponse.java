package org.scoula.domain.codef.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Codef Access Token 응답 객체")
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CodefTokenResponse(
	@ApiModelProperty(notes = "Access Token", example = "eyJhbGciOiJIUzI1Ni...", required = true)
	String accessToken,
	@ApiModelProperty(notes = "토큰 타입", example = "bearer", required = true)
	String tokenType,
	@ApiModelProperty(notes = "만료 시간", example = "3600", required = true)
	Integer expiresIn,
	@ApiModelProperty(notes = "스코프", example = "read", required = true)
	String scope
) {
}
