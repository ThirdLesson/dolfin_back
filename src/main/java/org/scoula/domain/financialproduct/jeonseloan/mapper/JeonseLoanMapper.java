package org.scoula.domain.financialproduct.jeonseloan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

import java.util.List;

@Mapper
public interface JeonseLoanMapper {

    // 저장
    void insertJeonseLoan(JeonseLoan jeonseLoan);

    // 배치 저장 (많은 양을 한꺼번에  저장, API 응답 처리 시 )
    void insertJeonseLoans(@Param("jeonseLoans") List<JeonseLoan> jeonseLoans);

    // 단건 조회
    JeonseLoan selectJeonseLoanById(Long jeonseLoanId);

    // 전체 목록 조회
    List<JeonseLoan> selectAllJeonseLoans();

    // 수정
    void updateJeonseLoan(JeonseLoan jeonseLoan);  // @Param 불필요

    // 금융회사별 전세대출 조회
    List<JeonseLoan> selectJeonseLoansByFinancialCompanyId(Long financialCompanyId);

    // 금리 범위로 검색
    List<JeonseLoan> selectJeonseLoansByInterestRateRange(
            @Param("minRate") Double minRate,
            @Param("maxRate") Double maxRate
    );

    // 상환 유형별 조회
    List<JeonseLoan> selectJeonseLoansByRepayType(String repayTypeName);

    // 중복 상품 존재 여부 확인 (API 동기화 시 사용)
    boolean existsByFinancialCompanyIdAndName(
            @Param("financialCompanyId") Long financialCompanyId,
            @Param("name") String name
    );
}
