package org.scoula.domain.financialCompany.jeonseLoan.service;

import org.scoula.domain.financialCompany.jeonseLoan.dto.request.JeonseLoanRequestDTO;
import org.scoula.domain.financialCompany.jeonseLoan.dto.response.JeonseLoanResponseDTO;

import java.util.List;

public interface JeonseLoanService {

    // 등록
    void createJeonseLoan(JeonseLoanRequestDTO requestDTO);

    // 단건 조회
    JeonseLoanResponseDTO getJeonseLoanById(Long jeonseLoanId);

    // 전체 목록 조회
    List<JeonseLoanResponseDTO> getAllJeonseLoans();

    // 수정
    void updateJeonseLoan(Long jeonseLoanId, JeonseLoanRequestDTO requestDTO);

    // 삭제
    void deleteJeonseLoan(Long jeonseLoanId);
}

