package org.scoula.domain.financialproduct.jeonseloan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.constants.JeonseLoanInterestFilterType;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

import java.util.List;

@Mapper
public interface JeonseLoanMapper {

	// 전체 리스트 조회
	List<JeonseLoanResponseDTO> getAllJeonseLoans(JeonseLoanInterestFilterType filterType);
}
