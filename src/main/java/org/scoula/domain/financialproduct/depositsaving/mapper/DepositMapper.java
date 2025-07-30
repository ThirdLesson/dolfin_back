package org.scoula.domain.financialproduct.depositsaving.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.constants.DepositSpclCondition;
import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.constants.SavingSpclCondition;
import org.scoula.domain.financialproduct.depositsaving.dto.response.DepositsResponse;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DepositMapper {

	// 	예금 상품 조회(체류 기간별 필터링, 금리 높은 순)
	List<Deposit> selectDepositsWithFilters(
		@Param("productPeriod") ProductPeriod productPeriod,
		@Param("spclConditions") List<DepositSpclCondition> spclConditions,
		@Param("remainTime") LocalDate remainTime,
		@Param("offset") int offset,
		@Param("limit") int limit
	);

	// 	상품 상세 정보 조회(상품 + 금융회사 정보)
	Deposit selectProductDetailById(@Param("id") Long id);

	int countDepositsWithFilters(
		@Param("productPeriod") ProductPeriod productPeriod,
		@Param("spclConditions") List<DepositSpclCondition> spclConditions,
		@Param("remainTime") LocalDate remainTime,
		@Param("remainMonths") int remainMonths
	);
	//  상품 Batch 저장
	void InsertBatch(List<Deposit> deposits);
}
