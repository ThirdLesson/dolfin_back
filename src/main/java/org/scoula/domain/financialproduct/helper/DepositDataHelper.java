package org.scoula.domain.financialproduct.helper;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.domain.financialproduct.depositsaving.dto.common.DepositProduct;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSpclCondition;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositDataHelper {

	public Map<String, Long> createdDepositidMap(List<Deposit> savedDeposits) {
		Map<String, Long> depositIdMap = savedDeposits.stream()
			.collect(Collectors.toMap(
				deposit -> deposit.getProductCode() + "_" + deposit.getCompanyCode() + "_" + deposit.getSaveMonth(),
				Deposit::getDepositId
			));
		return depositIdMap;
	}

	public DepositSpclCondition convertToSpclCondition(String conditionName, Deposit deposit) {
		DepositSpclConditionType conditionType = DepositSpclConditionType.fromApiValue(conditionName);

		if (conditionType == null) {
			conditionType = DepositSpclConditionType.fromKoreanName(conditionName);
		}
		return DepositSpclCondition.builder()
			.productCode(deposit.getProductCode())
			.companyCode(deposit.getCompanyCode())
			.spclCondition(conditionType)
			.saveMonth(deposit.getSaveMonth())
			.build();
	}
	public DepositSpclCondition mapDepositId(DepositSpclCondition condition,
		Map<String, Long> depositMap){
		String key = condition.getProductCode() + "_" + condition.getCompanyCode() + "_" + condition.getSaveMonth();
		Long depositId = depositMap.get(key);

		if(depositId == null){
			log.warn("매칭되는 Deposit을 찾을 수 없습니다 - productCode :{},companyCode:{},saveMonth :{}",condition.getProductCode(),condition.getCompanyCode(),condition.getSaveMonth());
			return null;
		}
		return DepositSpclCondition.builder()
			.depositId(depositId)
			.productCode(condition.getProductCode())
			.companyCode(condition.getCompanyCode())
			.spclCondition(condition.getSpclCondition())
			.saveMonth(condition.getSaveMonth())
			.build();
	}

	public Deposit convertToDepositEntity(DepositProduct product,int period, FinancialCompany company){
		BigDecimal interestRate = safeParseBigDecimal(product.interestRate());
		BigDecimal maxInterestRate = safeParseBigDecimal(product.primeInterestRate());

		return Deposit.builder()
			.productCode(product.code())
			.companyCode(product.companyCode())
			.financialCompanyId(company.getFinancialCompanyId())
			.name(product.name())
			.saveMonth(period)
			.interestRate(interestRate)
			.maxInterestRate(maxInterestRate)
			.build();
	}

	public boolean isValidProduct(DepositProduct product, Map<String, FinancialCompany> companyMap){
		FinancialCompany company = companyMap.get(product.companyCode());
		if(company == null){
			return false;
		}

		BigDecimal interestRate = safeParseBigDecimal(product.interestRate());
		if(interestRate.compareTo(BigDecimal.ZERO) <= 0){
			return false;
		}
		return true;
	}
	public BigDecimal safeParseBigDecimal(String value){
		if(value == null || value.trim().isEmpty()){
			return BigDecimal.ZERO;
		}
		try {
			return new BigDecimal(value.trim());
		}catch (NumberFormatException e){
			return BigDecimal.ZERO;
		}
	}
	public List<DepositSpclCondition> mapSpclConditionsWithDepositId(
		List<DepositSpclCondition> spclConditions,
		Map<String,Long> depositMap){

		return spclConditions.stream()
			.map(condition -> mapDepositId(condition,depositMap))
			.filter(Objects::nonNull)
			.toList();
	}

}
