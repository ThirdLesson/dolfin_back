package org.scoula.domain.financialproduct.depositsaving.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.constants.SavingSpclConditionType;
import org.scoula.domain.financialproduct.depositsaving.entity.Saving;
import org.scoula.domain.financialproduct.depositsaving.entity.SavingSpclCondition;

@Mapper
public interface SavingMapper {
	// 적금상품저장
	void insertBatch(@Param("savings") List<Saving> savings);

	// 우대조건 저장 // 적금 상품 리스트 조회(필터링)
	void insertSpclCondition(@Param("spclConditions") List<SavingSpclCondition> conditionWithSavingId);

	// 적금 상품 단건 상세 조회 (상품 + 금융회사 정보)
	Saving selectProductDetailById(@Param("id") Long id);

	// 상품 코드 + 회사코드 + 기간 기준 중복 조회
	Saving selectByProductAndCompanyCode(
		@Param("productCode") String productCode,
		@Param("companyCode") String companyCode,
		@Param("saveMonth") Integer saveMonth);

	// 적금 상품 우대 조건 조회 (1:N)
	List<SavingSpclCondition> selectSpclConditionsBySavingId(Long savingId);

	// // 전체 적금 상품 리스트 조회(정렬/페이징 포함, 필터 없음)
	List<Saving> selectAllSavings(
		@Param("offset") int offset,
		@Param("limit") int limit);

	// // 전체 적금 상품 개수(필터 없음)
	int countAllSavings();

	// 체류기간 + 조건별 예금 상품 목록 조회
	// 	적금 상품 조회(체류 기간별 필터링, 금리 높은 순)
	List<Saving> selectSavingWithFilters(
		@Param("spclConditions") List<SavingSpclConditionType> spclConditions,
		@Param("remainTime") int remainTime,
		@Param("offset") int offset,
		@Param("limit") int limit
		);

	// 조건에 맞는 예금 상품 개수
	int countSavingWithFilters(
		@Param("spclConditions") List<SavingSpclConditionType> spclConditions,
		@Param("remainTime") int remainTime
		);

}
