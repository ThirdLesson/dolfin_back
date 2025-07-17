package org.scoula.domain.financialCompany.depositSaving.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.scoula.domain.financialCompany.depositSaving.entity.DepositSaving;

import java.util.List;

@Mapper
public interface DepositSavingMapper {

    //   저장
    void insertDepositSaving(DepositSaving depositSaving);

    //   하나 조회
    DepositSaving selectDepositSavingById(Long depositSavingId);

    // 전체 목록 조회
    List<DepositSaving> selectAllDepositSavings();

    // 수정
    void updateDepositSaving(DepositSaving depositSaving);

    //    삭제
    void deleteDepositSaving(Long depositSavingId);
}
