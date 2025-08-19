package org.scoula.domain.financialproduct.jeonseloan.service;

import org.scoula.domain.financialproduct.jeonseloan.dto.request.JeonseLoanRequestDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanDetailResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanResponseDTO;
import org.scoula.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JeonseLoanService {

	Page<JeonseLoanResponseDTO> getAllJeonseLoans(JeonseLoanRequestDTO request, Pageable pageable);

	JeonseLoanDetailResponseDTO getJeonseLoanDetail(Long jeonseLoanId, Member member);
}


