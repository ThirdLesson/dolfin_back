package org.scoula.domain.financialproduct.depositsaving.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.constants.SavingSpclCondition;
import org.scoula.domain.financialproduct.depositsaving.entity.Saving;

@Mapper
public interface SavingMapper {
	List<Saving> selectSavingWithFilters(
		@Param("productPeriod") ProductPeriod productPeriod,
		@Param("spclConditions") List<SavingSpclCondition> spclConditions,
		@Param("remainTime") LocalDate remainTime,
		@Param("offset") int offeset,
		@Param("limit") int limit,
		int pageSize);

	int countSavingWithFilters(
		@Param("productPeriod") ProductPeriod productPeriod,
		@Param("spclConditions") List<SavingSpclCondition> spclConditions,
		@Param("remainTime") LocalDate remainTime,
		@Param("remainMonths") int remainMonths, int pageSize);
}
