package org.scoula.domain.financialproduct.jeonseloan.service;

import org.scoula.domain.financialproduct.jeonseloan.dto.request.JeonseLoanRequestDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanDetailResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanResponseDTO;
import org.scoula.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JeonseLoanService {

	// 전세대출 전체 리스트 조회
	Page<JeonseLoanResponseDTO> getAllJeonseLoans(JeonseLoanRequestDTO request, Pageable pageable);

	// 전세대출 상세 조회
	JeonseLoanDetailResponseDTO getJeonseLoanDetail(Long jeonseLoanId, Member member);
}


