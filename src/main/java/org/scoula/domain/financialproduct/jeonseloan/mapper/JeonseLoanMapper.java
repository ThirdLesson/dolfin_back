package org.scoula.domain.financialproduct.jeonseloan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.jeonseloan.dto.request.JeonseLoanRequestDTO;
import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

import java.util.List;
import java.util.Optional;

@Mapper
public interface JeonseLoanMapper {

	List<JeonseLoan> findJeonseLoansWithFilter(
		@Param("request") JeonseLoanRequestDTO request,
		@Param("offset") int offset,
		@Param("limit") int limit);

	int countJeonseLoansWithFilter(@Param("request") JeonseLoanRequestDTO request);


	Optional<JeonseLoan> findById(
		@Param("jeonseLoanId") Long jeonseLoanId);
}
