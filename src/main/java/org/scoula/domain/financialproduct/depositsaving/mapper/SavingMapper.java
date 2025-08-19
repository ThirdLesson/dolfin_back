package org.scoula.domain.financialproduct.depositsaving.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.constants.SavingSpclConditionType;
import org.scoula.domain.financialproduct.depositsaving.entity.Saving;
import org.scoula.domain.financialproduct.depositsaving.entity.SavingSpclCondition;

@Mapper
public interface SavingMapper {
	void insertBatch(@Param("savings") List<Saving> savings);

	void insertSpclCondition(@Param("spclConditions") List<SavingSpclCondition> conditionWithSavingId);

	Saving selectProductDetailById(@Param("id") Long id);

	Saving selectByProductAndCompanyCode(
		@Param("productCode") String productCode,
		@Param("companyCode") String companyCode,
		@Param("saveMonth") Integer saveMonth);

	List<SavingSpclCondition> selectSpclConditionsBySavingId(Long savingId);

	List<Saving> selectAllSavings(
		@Param("offset") int offset,
		@Param("limit") int limit);

	int countAllSavings();

	
	List<Saving> selectSavingWithFilters(
		@Param("spclConditions") List<SavingSpclConditionType> spclConditions,
		@Param("remainTime") int remainTime,
		@Param("offset") int offset,
		@Param("limit") int limit
		);

	int countSavingWithFilters(
		@Param("spclConditions") List<SavingSpclConditionType> spclConditions,
		@Param("remainTime") int remainTime
		);

}
