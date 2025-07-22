package org.scoula.domain.codef.dto.common;

public record CodefCommonResponse<T>(
	CodefResult result,
	T data
) {
}
