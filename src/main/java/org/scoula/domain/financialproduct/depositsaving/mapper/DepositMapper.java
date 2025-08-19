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

	void insertBatch(@Param("deposits") List<Deposit> deposits);
	void insertSpclCondition(@Param("spclConditions") List<DepositSpclCondition> conditions);

	Deposit selectProductDetailById(@Param("id") Long id);
	Deposit selectByProductAndCompanyCode(
		@Param("productCode") String productCode,
		@Param("companyCode") String companyCode,
		@Param("saveMonth") Integer saveMonth);
	List<DepositSpclCondition> selectSpclConditionsByDepositId(Long depositId);

	List<Deposit> selectAllDeposits(
		@Param("offset") int offset,
		@Param("limit") int limit);
	int countAllDeposits();

	
	List<Deposit> selectDepositsWithFilters(
		@Param("spclConditions") List<DepositSpclConditionType> spclConditions,
		@Param("remainTime") int remainTime,
		@Param("offset") int offset,
		@Param("limit") int limit
	);
	int countDepositsWithFilters(
		@Param("spclConditions") List<DepositSpclConditionType> spclConditions,
		@Param("remainTime") int remainTime
	);

}
