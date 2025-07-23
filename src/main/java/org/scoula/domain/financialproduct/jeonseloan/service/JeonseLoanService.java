package org.scoula.domain.financialproduct.jeonseloan.service;

import org.scoula.domain.financialproduct.jeonseloan.dto.JeonseLoanDTO;

import java.util.List;

public interface JeonseLoanService {

    // 등록
    void createJeonseLoan(JeonseLoanDTO jeonseLoanDTO);

    // 배치 등록 (API 응답 처리용)
    void createJeonseLoans(List<JeonseLoanDTO> jeonseLoanDTOs);

    // 단건 조회
    JeonseLoanDTO getJeonseLoanById(Long jeonseLoanId);

    // 전체 목록 조회
    List<JeonseLoanDTO> getAllJeonseLoans();

    // 수정
    void updateJeonseLoan(Long jeonseLoanId, JeonseLoanDTO requestDTO);

    // 금융회사별 전세대출 조회
    List<JeonseLoanDTO> getJeonseLoansByFinancialCompanyId(Long financialCompanyId);

    // 금리 범위로 검색
    List<JeonseLoanDTO> getJeonseLoansByInterestRateRange(Double minRate, Double maxRate);

    // 상환 유형별 조회
    List<JeonseLoanDTO> getJeonseLoansByRepayType(String repayTypeName);

    // 외부 API에서 전세대출 데이터 가져와서 저장
    void fetchAndSaveJeonseLoansFromApi();

    // 특정 금융회사의 데이터만 API에서 가져와서 저장
    void fetchAndSaveJeonseLoansFromApi(Long financialCompanyId);

    // API 데이터 동기화 (기존 데이터 업데이트/새 데이터 추가)
    void synchronizeJeonseLoansFromApi();
}


