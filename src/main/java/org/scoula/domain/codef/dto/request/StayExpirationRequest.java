package org.scoula.domain.codef.dto.request;

import lombok.Builder;

@Builder
public record StayExpirationRequest(
	String organization,
	String passportNo,
	String nationality,
	String country,
	String birthDate
) {
}

