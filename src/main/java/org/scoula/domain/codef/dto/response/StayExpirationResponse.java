package org.scoula.domain.codef.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StayExpirationResponse(
	String resAuthenticity,
	String resAuthenticityDesc,
	String resPassportNo,
	String resNationality,
	String commBirthDate,
	String resStatus,
	String resExpirationDate
) {
}
