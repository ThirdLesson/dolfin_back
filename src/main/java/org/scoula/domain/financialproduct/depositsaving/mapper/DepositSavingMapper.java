package org.scoula.domain.financialproduct.depositsaving.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSaving;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSavingType;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface DepositSavingMapper {

	// 전체 목록 조회
	List<DepositSaving> selectAllDepositSavings();

	// 단건 조회
	DepositSaving selectDepositSavingById(@Param("depositSavingId") Long depositSavingId);

	// 등록
	int insertDepositSaving(DepositSaving depositSaving);

	// 수정
	int updateDepositSaving(DepositSaving depositSaving);

	// 삭제
	int deleteDepositSaving(Long depositSavingId);

	//  조건 별 조회

	// 타입별 조회
	List<DepositSaving> selectByType(@Param("type") DepositSavingType type);

	//  금융회사별 조회
	List<DepositSaving> selectByFinancialCompany(@Param("financialCompanyId") Long financialCompanyId);

	// 금리별로 조회
	List<DepositSaving> selectByInterestRateRange(@Param("minRate") BigDecimal minRate,
		@Param("maxRate") BigDecimal maxRate);

	//  저축기간별 조회
	List<DepositSaving> selectBySaveMonth(@Param("saveMonth") Integer saveMonth);

	// 금융회사 + 타입별 조회
	List<DepositSaving> selectByCompanyAndType(@Param("financialCompanyId") Long financialCompanyId,
		@Param("type") DepositSavingType type);

	//  배치 삽입 (대량 데이터 처리 시 성능 향상)
	int insertBatch(List<DepositSaving> depositSavings);
}
