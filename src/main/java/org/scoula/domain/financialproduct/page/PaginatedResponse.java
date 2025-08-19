package org.scoula.domain.financialproduct.page;

import java.util.List;

import org.springframework.data.domain.Page;

public record PaginatedResponse<T>(
	List<T> content,
	int totalPages, 
	long totalCount
) {
	public static <T> PaginatedResponse<T> from(Page<T> page) {
		return new PaginatedResponse<>(
			page.getContent(),
			page.getTotalPages(),
			page.getTotalElements()
		);
	}
}
