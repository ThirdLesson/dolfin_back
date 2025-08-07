package org.scoula.domain.financialproduct.depositsaving.service;

import static org.scoula.domain.financialproduct.constants.ProductPeriod.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.constants.SavingSpclConditionType;
import org.scoula.domain.financialproduct.depositsaving.dto.common.DepositProduct;
import org.scoula.domain.financialproduct.depositsaving.dto.response.SavingsResponse;
import org.scoula.domain.financialproduct.depositsaving.entity.Saving;
import org.scoula.domain.financialproduct.depositsaving.entity.SavingSpclCondition;
import org.scoula.domain.financialproduct.depositsaving.gpt.GptEligibilityChecker;
import org.scoula.domain.financialproduct.depositsaving.mapper.SavingMapper;
import org.scoula.domain.financialproduct.exception.FinancialErrorCode;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.financialproduct.financialcompany.mapper.FinancialCompanyMapper;
import org.scoula.domain.financialproduct.financialcompany.service.FinancialCompanyService;
import org.scoula.domain.financialproduct.helper.SavingApiHelper;
import org.scoula.domain.financialproduct.helper.SavingDataHelper;
import org.scoula.domain.member.entity.Member;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavingServiceImpl implements SavingService {

	private final SavingMapper savingMapper;
	private final FinancialCompanyMapper financialCompanyMapper;
	private final SavingDataHelper savingDataHelper;
	private final SavingApiHelper savingApiHelper;
	private final FinancialCompanyService financialCompanyService;
	private final GptEligibilityChecker gptEligibilityChecker;

	// 적금 상품 정보 저장
	@Override
	@Transactional
	public List<Saving> fetchAndSaveSavings() {
		Map<String, FinancialCompany> companyMap = createCompanyMap();
		List<Saving> savings = collectSavingData(companyMap);
		List<Saving> saveSavings = saveSavingsToDatabase(savings);
		return saveSavings;
	}

	// 우대 조건 저장
	@Override
	@Transactional
	public void fetchAndSaveSpclConditions(List<Saving> savedSavings) {
		if (savedSavings.isEmpty())
			return;

		Map<String, Long> savingIdMap = savingDataHelper.createdSavingidMap(savedSavings);

		List<SavingSpclCondition> spclConditions = collectSpclConditionData(savedSavings);

		saveSpclConditionsToDatabase(spclConditions, savingIdMap);
	}

	// 적금 상품 리스트 조회(필터링)
	@Override
	@Transactional(readOnly = true)
	public Page<SavingsResponse> getSavings(ProductPeriod productPeriod, List<SavingSpclConditionType> spclConditions,
		Pageable pageable, Member member) {
		// 멤버 체류기간 가져오기
		Integer remainTime = null;
		if (productPeriod.equals(STAY_EXPIRATION)) {
			remainTime = Math.max(0, (int)ChronoUnit.MONTHS.between(LocalDate.now(), member.getRemainTime()));
		} else {
			remainTime = productPeriod.getMonth();
		}

		int totalCount = savingMapper.countSavingWithFilters(spclConditions, remainTime);

		// 기간별,조건별 필터링, pageable
		List<Saving> savings = savingMapper.selectSavingWithFilters(
			spclConditions, remainTime, (int)pageable.getOffset(), pageable.getPageSize()
		);

		List<SavingsResponse> savingsResponses = savings.stream()
			.map(this::convertToSavingsResponse)
			.toList();
		return new PageImpl<>(savingsResponses, pageable, totalCount);

	}

	@Override
	@Transactional(readOnly = true)
	public SavingsResponse getSavingDetail(Long savingId) {
		Saving saving = savingMapper.selectProductDetailById(savingId);
		if (saving == null) {
			throw new CustomException(FinancialErrorCode.DEPOSIT_SAVING_NOT_FOUND, LogLevel.WARNING, null, null);
		}
		FinancialCompany company = financialCompanyService.getById(saving.getFinancialCompanyId());
		List<SavingSpclCondition> savingConditions = savingMapper.selectSpclConditionsBySavingId(savingId);
		return SavingsResponse.of(saving, company, savingConditions);
	}

	@Override
	public Page<SavingsResponse> getAllSavings(Pageable pageable) {
		int totalCount = savingMapper.countAllSavings();

		List<Saving> savings = savingMapper.selectAllSavings(
			(int)pageable.getOffset(), pageable.getPageSize()
		);
		List<SavingsResponse> savingsResponses = savings.stream()
			.map(this::convertToSavingsResponse)
			.toList();
		return new PageImpl<>(savingsResponses, pageable, totalCount);
	}

	private Map<String, FinancialCompany> createCompanyMap() {
		List<FinancialCompany> companies = financialCompanyMapper.selectAllFinancialCompany();
		Map<String, FinancialCompany> companyMap = new HashMap<>();
		companies.forEach(company -> companyMap.put(company.getCode(), company));
		return companyMap;
	}

	private List<Saving> collectSavingData(Map<String, FinancialCompany> companyMap) {
		List<Saving> savings = new ArrayList<>();
		Set<String> processedProducts = new HashSet<>(); // 중복방지

		// 	기간별 데이터 수집
		List<Integer> periods = List.of(
			ProductPeriod.SIX_MONTH.getMonth(),
			ProductPeriod.ONE_YEAR.getMonth(),
			ProductPeriod.TWO_YEAR.getMonth()
		);
		for (Integer period : periods) {
			collectProductsByPeriod(period, companyMap, savings, processedProducts);
		}
		return savings;
	}

	private int collectProductsByPeriod(int period, Map<String, FinancialCompany> companyMap,
		List<Saving> savings, Set<String> processedProducts) {

		int offset = 0;
		int periodProductCount = 0;
		int consecutiveEmptyCount = 0;
		final int MAX_EMPTY_RESPONSES = 3;

		while (consecutiveEmptyCount < MAX_EMPTY_RESPONSES) {
			List<DepositProduct> products = null;
			try {
				products = savingApiHelper.getProductsByPeriod(period, offset);
			} catch (Exception e) {
				consecutiveEmptyCount++;
				offset += 20;
				continue;
			}
			if (products == null || products.isEmpty()) {
				consecutiveEmptyCount++;
				offset += 20;
				continue;
			}
			consecutiveEmptyCount = 0;

			for (DepositProduct product : products) {
				if (!companyMap.containsKey(product.companyCode())) {
					continue;
				}
				String productKey = product.companyCode() + "_" + product.code() + "_" + period;
				// 중복 체크
				if (processedProducts.contains(productKey)) {
					continue;
				}
				// 유효성 체크
				if (!savingDataHelper.isValidProduct(product, companyMap)) {
					continue;
				}

				processedProducts.add(productKey);
				FinancialCompany company = companyMap.get(product.companyCode());

				SavingApiHelper.ProductDetailInfo detailInfo;
				try {
					detailInfo = savingApiHelper.getProductDetailInfo(product.code());
				} catch (Exception e) {
					continue;
				}
				// // 외국인 가입 가능 여부 확인
				boolean isEligible = true;
				try {
					Map<String, Object> productData = createProductDataForGpt(product, detailInfo);
					isEligible = gptEligibilityChecker.isEligibleForForeigner(productData);
				} catch (Exception e) {
					continue;
				}
				if (!isEligible) {
					continue;
				}
				Saving saving = savingDataHelper.convertToSavingEntity(product, period, company);
				savings.add(saving);
				periodProductCount++;
			}
			offset += 20;
		}
		return periodProductCount;
	}

	private Map<String, Object> createProductDataForGpt(DepositProduct product,
		SavingApiHelper.ProductDetailInfo detailInfo) {
		Map<String, Object> result = new HashMap<>();
		result.put("name", product.name());
		result.put("joinTarget", product.joinTarget() != null ? product.joinTarget() : "실명의 개인");
		result.put("note", product.note() != null ? product.note() : "");
		result.put("specialConditions", detailInfo.specialConditions());
		result.put("productCategories", product.productCategories() != null ? product.productCategories() : List.of());

		return Map.of("result", result);
	}

	private List<SavingSpclCondition> collectSpclConditionData(List<Saving> savedSavings) {
		List<SavingSpclCondition> spclConditions = new ArrayList<>();

		for (Saving saving : savedSavings) {
			try {
				SavingApiHelper.ProductDetailInfo detailInfo = savingApiHelper.getProductDetailInfo(
					saving.getProductCode());
				detailInfo.specialConditions().stream()
					.map(conditionName -> {
						SavingSpclCondition condition = savingDataHelper.convertToSpclCondition(conditionName, saving);
						return condition;
					})
					.filter(Objects::nonNull)
					.forEach(spclConditions::add);
			} catch (Exception e) {
				log.warn("상품 {}의 우대조건 수집 실패: {}", saving.getProductCode(), e.getMessage());
				continue;
			}
		}
		return spclConditions;
	}

	private List<Saving> saveSavingsToDatabase(List<Saving> savings) {
		if (savings.isEmpty())
			return Collections.emptyList();

		savingMapper.insertBatch(savings);

		return savings.stream()
			.map(saving -> savingMapper.selectByProductAndCompanyCode(
				saving.getProductCode(), saving.getCompanyCode(), saving.getSaveMonth()))
			.filter(Objects::nonNull)
			.toList();
	}

	private void saveSpclConditionsToDatabase(List<SavingSpclCondition> spclConditions,
		Map<String, Long> savingIdMap) {
		if (spclConditions.isEmpty())
			return;

		List<SavingSpclCondition> validConditions = spclConditions.stream()
			.filter(condition -> condition.getSpclCondition() != null)  // null 제거
			.toList();

		if (validConditions.isEmpty())
			return;

		List<SavingSpclCondition> conditionWithSavingId =
			savingDataHelper.mapSpclConditionsWithSavingId(validConditions, savingIdMap);

		if (!conditionWithSavingId.isEmpty()) {
			savingMapper.insertSpclCondition(conditionWithSavingId);
		}
	}

	private SavingsResponse convertToSavingsResponse(Saving saving) {
		FinancialCompany company = financialCompanyService.getById(saving.getFinancialCompanyId());

		List<SavingSpclCondition> savingConditions =
			savingMapper.selectSpclConditionsBySavingId(saving.getSavingId());

		return SavingsResponse.of(saving, company, savingConditions);
	}
}
