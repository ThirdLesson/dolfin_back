package org.scoula.domain.financialproduct.jeonseloan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.jeonseloan.dto.request.JeonseLoanRequestDTO;
import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

import java.util.List;
import java.util.Optional;

@Mapper
public interface JeonseLoanMapper {

	// 리스트 전체 필터링 조회
	List<JeonseLoan> findJeonseLoansWithFilter(
		@Param("request") JeonseLoanRequestDTO request,
		@Param("offset") int offset,
		@Param("limit") int limit);

	// 총 개수 조회
	int countJeonseLoansWithFilter(@Param("request") JeonseLoanRequestDTO request);


	// 전세 대출 리스트 상세조회
	Optional<JeonseLoan> findById(
		@Param("jeonseLoanId") Long jeonseLoanId);
}
