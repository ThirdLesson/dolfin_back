package org.scoula.domain.financialCompany.jeonseLoan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialCompany.jeonseLoan.entity.JeonseLoan;

import java.util.List;

@Mapper
public interface JeonseLoanMapper {

    // 저장
    void insertJeonseLoan(JeonseLoan jeonseLoan);

    // 단건 조회
    JeonseLoan selectJeonseLoanById(Long jeonseLoanId);

    // 전체 목록 조회
    List<JeonseLoan> selectAllJeonseLoans();

    // 수정
    void updateJeonseLoan(@Param("jeonseLoanId") Long jeonseLoanId, @Param("jeonseLoan") JeonseLoan jeonseLoan);

    // 삭제
    void deleteJeonseLoan(Long jeonseLoanId);
}
