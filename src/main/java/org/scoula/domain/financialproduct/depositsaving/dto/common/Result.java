package org.scoula.domain.financialproduct.depositsaving.dto.common;

import java.util.List;

public record Result<T>(
	int totalCount,
	int offSet,
	int size,
	List<T> products
) {
}
