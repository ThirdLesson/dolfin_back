package org.scoula.domain.financialproduct.jeonseloan.service;

import org.scoula.domain.financialproduct.constants.JeonseLoanInterestFilterType;
import org.scoula.domain.financialproduct.jeonseloan.dto.JeonseLoanDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

import java.util.List;

public interface JeonseLoanService {

	// 전세대출 전체 리스트 조회
	List<JeonseLoanResponseDTO> getAllJeonseLoans(JeonseLoanInterestFilterType filterType);

}


