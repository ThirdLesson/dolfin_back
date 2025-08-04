package org.scoula.domain.financialproduct.depositsaving.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSpclCondition;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DepositMapper {

	//  예금상품 저장
	void insertBatch(@Param("deposits") List<Deposit> deposits);
	// 우대조건 저장 // 예금 상품 리스트 조회(필터링)
	void insertSpclCondition(@Param("spclConditions") List<DepositSpclCondition> conditions);

	// 	예금 상품 단건 상세 조회(상품 + 금융회사 정보)
	Deposit selectProductDetailById(@Param("id") Long id);
	// 상품 코드 + 회사코드 + 기간 기준 중복 조회
	Deposit selectByProductAndCompanyCode(
		@Param("productCode") String productCode,
		@Param("companyCode") String companyCode,
		@Param("saveMonth") Integer saveMonth);
	// 예금 상품 우대 조건 조회(1:N)
	List<DepositSpclCondition> selectSpclConditionsByDepositId(Long depositId);

	// 전체 예금 상품 리스트 조회(정렬/페이징 포함, 필터 없음)
	List<Deposit> selectAllDeposits(
		@Param("offset") int offset,
		@Param("limit") int limit);
	// 전체 예금 상품 개수(필터 없음)
	int countAllDeposits();

	// 체류기간 + 조건별 예금 상품 목록 조회
	// 	예금 상품 조회(체류 기간별 필터링, 금리 높은 순)
	List<Deposit> selectDepositsWithFilters(
		@Param("spclConditions") List<DepositSpclConditionType> spclConditions,
		@Param("remainTime") int remainTime,
		@Param("offset") int offset,
		@Param("limit") int limit
	);
	// 조건에 맞는 예금 상품 개수
	int countDepositsWithFilters(
		@Param("spclConditions") List<DepositSpclConditionType> spclConditions,
		@Param("remainTime") int remainTime
	);

}
