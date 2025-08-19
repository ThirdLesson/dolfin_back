package org.scoula.domain.financialproduct.helper;

import static java.util.Locale.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.scoula.domain.financialproduct.constants.SavingSpclConditionType;
import org.scoula.domain.financialproduct.depositsaving.dto.common.DepositProduct;
import org.scoula.domain.financialproduct.depositsaving.entity.Saving;
import org.scoula.domain.financialproduct.depositsaving.entity.SavingSpclCondition;
import org.scoula.domain.financialproduct.depositsaving.gpt.GptEligibilityChecker;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SavingDataHelper {

	private final GptEligibilityChecker gptEligibilityChecker;

	public Map<String, Long> createdSavingidMap(List<Saving> savedSavings) {
		Map<String, Long> savingIdMap = savedSavings.stream()
			.collect(Collectors.toMap(
				saving ->saving.getProductCode() + "_" + saving.getCompanyCode() + "_" + saving.getSaveMonth(),
				Saving::getSavingId
			));
		return savingIdMap;
	}

	public SavingSpclCondition convertToSpclCondition(String conditionName, Saving saving) {
		Map<String, Object> conditionData = Map.of("result", Map.of(
			"joinTarget", "", "note", "", "specialConditions", List.of(conditionName), "productCategories", List.of()
		));

		SavingSpclConditionType conditionType = SavingSpclConditionType.fromApiValue(conditionName);
		if (conditionType == null) {
			conditionType = SavingSpclConditionType.fromKoreanName(conditionName);
		}
		if (conditionType == null) {
			conditionType = SavingSpclConditionType.ETC;
		} else {
			log.info("우대 조건 매핑 성공 : {} -> {}", conditionName, conditionType);
		}
		return SavingSpclCondition.builder()
			.productCode(saving.getProductCode())
			.companyCode(saving.getCompanyCode())
			.spclCondition(conditionType)
			.saveMonth(saving.getSaveMonth())
			.build();

	}
	public static SavingSpclCondition mapSavingId(SavingSpclCondition condition,
		Map<String, Long> savingMap) {
		String key = condition.getProductCode() + "_" + condition.getCompanyCode() + "_" + condition.getSaveMonth();
		Long savingId = savingMap.get(key);

		if (savingId == null) {
			log.warn("매칭되는 Saving을 찾을 수 없습니다 - productCode :{},companyCode:{},saveMonth :{}",
				condition.getProductCode(), condition.getCompanyCode(), condition.getSaveMonth());
			return null;
		}
		return SavingSpclCondition.builder()
			.savingId(savingId)
			.productCode(condition.getProductCode())
			.companyCode(condition.getCompanyCode())
			.spclCondition(condition.getSpclCondition())
			.saveMonth(condition.getSaveMonth())
			.build();
	}

	public Saving convertToSavingEntity(DepositProduct product, int period, FinancialCompany company) {
		BigDecimal interestRate = safeParseBigDecimal(product.interestRate());
		BigDecimal maxInterestRate = safeParseBigDecimal(product.primeInterestRate());

		return Saving.builder()
			.productCode(product.code())
			.companyCode(product.companyCode())
			.financialCompanyId(company.getFinancialCompanyId())
			.name(product.name())
			.saveMonth(period)
			.interestRate(interestRate)
			.maxInterestRate(maxInterestRate)
			.build();
	}

	public boolean isValidProduct(DepositProduct product, Map<String, FinancialCompany> companyMap) {
		if (product.name() == null || product.name().trim().isEmpty())
			return false;
		if (product.code() == null || product.code().trim().isEmpty())
			return false;
		if (product.companyCode() == null || product.companyCode().trim().isEmpty())
			return false;
		if (!companyMap.containsKey(product.companyCode()))
			return false;

		BigDecimal rate = parseInterestRate(product.interestRate());
		return rate.compareTo(BigDecimal.ZERO) > 0;
	}

	public BigDecimal safeParseBigDecimal(String value) {
		if (value == null || value.trim().isEmpty()) {
			return BigDecimal.ZERO;
		}
		try {
			return new BigDecimal(value.trim());
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
	}

	public static List<SavingSpclCondition> mapSpclConditionsWithSavingId(
		List<SavingSpclCondition> spclConditions,
		Map<String, Long> savingMap) {

		return spclConditions.stream()
			.map(condition -> mapSavingId(condition, savingMap))
			.filter(Objects::nonNull)
			.toList();
	}

	private SavingSpclConditionType mapSpecialConditionType(String conditionName) {
		SavingSpclConditionType type = SavingSpclConditionType.fromApiValue(conditionName);
		if (type != null)
			return type;

		type = SavingSpclConditionType.fromKoreanName(conditionName);
		if (type != null)
			return type;

		String lower = conditionName.toLowerCase();
		if (lower.contains("비대면") || lower.contains("온라인") || lower.contains("인터넷"))
			return SavingSpclConditionType.ONLINE;
		if (lower.contains("앱") || lower.contains("스마트폰") || lower.contains("모바일"))
			return SavingSpclConditionType.BANK_APP;
		if (lower.contains("급여") || lower.contains("월급"))
			return SavingSpclConditionType.USING_SALARY_ACCOUNT;
		if (lower.contains("공과금") || lower.contains("전기") || lower.contains("수도") || lower.contains("가스"))
			return SavingSpclConditionType.USING_UTILITY_BILL;
		if (lower.contains("카드") || lower.contains("신용카드") || lower.contains("체크카드"))
			return SavingSpclConditionType.USING_CARD;
		if (lower.contains("첫거래") || lower.contains("신규") || lower.contains("처음"))
			return SavingSpclConditionType.FIRST_BANKING;
		if (lower.contains("입출금") || lower.contains("통장") || lower.contains("연계계좌"))
			return SavingSpclConditionType.DEPOSIT_ACCOUNT;
		if (lower.contains("청약"))
			return SavingSpclConditionType.HOUSING_SUBSCRIPTION;
		if (lower.contains("추천") || lower.contains("쿠폰") || lower.contains("코드"))
			return SavingSpclConditionType.RECOMMEND_COUPON;
		if (lower.contains("자동이체") || lower.contains("자동 납입") || lower.contains("정기이체"))
			return SavingSpclConditionType.AUTO_DEPOSIT;
		if (lower.contains("pension") || lower.contains("연금") || lower.contains("펜션"))
			return SavingSpclConditionType.PENSION;

		return null;
	}

	public BigDecimal parseInterestRate(String value) {
		if (value == null || value.trim().isEmpty()) {
			return BigDecimal.ZERO;
		}
		try {
			return new BigDecimal(value.replace("%", "").trim());
		} catch (NumberFormatException e) {
			log.warn(" 금리 파싱 실패: {}", value);
			return BigDecimal.ZERO;
		}
	}
	public String generateProductKey(String companyCode, String productCode, Integer period) {
		return companyCode + "_" + productCode + "_" + period;
	}
}
