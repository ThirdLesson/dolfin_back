package org.scoula.domain.financialproduct.depositsaving.dto.common;

public record ProductListResponse<T>(
	boolean isSuccess,
	String detailCode,
	String message,
	Result<T> result
) {
}
