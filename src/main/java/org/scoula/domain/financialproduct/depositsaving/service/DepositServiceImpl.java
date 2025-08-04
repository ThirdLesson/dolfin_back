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

import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.depositsaving.dto.common.DepositProduct;
import org.scoula.domain.financialproduct.depositsaving.dto.response.DepositsResponse;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSpclCondition;
import org.scoula.domain.financialproduct.depositsaving.mapper.DepositMapper;
import org.scoula.domain.financialproduct.exception.FinancialErrorCode;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.financialproduct.financialcompany.mapper.FinancialCompanyMapper;
import org.scoula.domain.financialproduct.financialcompany.service.FinancialCompanyService;
import org.scoula.domain.financialproduct.helper.DepositApiHelper;
import org.scoula.domain.financialproduct.helper.DepositDataHelper;
import org.scoula.domain.member.entity.Member;

import org.scoula.global.exception.CustomException;
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
public class DepositServiceImpl implements DepositService {

	private final DepositMapper depositMapper;
	private final FinancialCompanyMapper financialCompanyMapper;
	private final DepositApiHelper apiHelper;
	private final DepositDataHelper dataHelper;
	private final FinancialCompanyService financialCompanyService;

	// 예금 상품 정보 저장
	@Override
	@Transactional
	public List<Deposit> fetchAndSaveDeposits() {
		log.info("예금 상품 수집 시작");
		Map<String, FinancialCompany> companyMap = createCompanyMap(); //금융회사매핑
		List<Deposit> deposits = collectDepositData(companyMap); // 외부API 데이터 수집
		List<Deposit> saveDeposits = saveDepositsToDatabase(deposits); // DB에 저장
		log.info("예금 상품 저장 완료 :{}", saveDeposits.size());
		return saveDeposits;
	}

	// 우대 조건 저장
	@Override
	@Transactional
	public void fetchAndSaveSpclConditions(List<Deposit> savedDeposits) {
		log.info("우대조건 수집 시작");
		if (savedDeposits.isEmpty()) {
			log.warn("저장된 예금 상품이 없어 우대조건을 수집할 수 없습니다.");
			return;
		}
		Map<String, Long> depositIdMap = dataHelper.createdDepositidMap(savedDeposits);
		List<DepositSpclCondition> spclConditions = collectSpclConditionData(savedDeposits);
		saveSpclConditionsToDatabase(spclConditions, depositIdMap);
		log.info("우대조건 저장 완료");
	}

	// 예금 상품 리스트 조회(필터링)
	@Override
	@Transactional(readOnly = true)
	public Page<DepositsResponse> getDeposits(ProductPeriod productPeriod,
		List<DepositSpclConditionType> spclConditions,
		Pageable pageable, Member member) {
		Integer remainTime = null;
		if(productPeriod.equals(STAY_EXPIRATION)){
			remainTime = Math.max(0,(int) ChronoUnit.MONTHS.between(LocalDate.now(), member.getRemainTime()));
		}else{
			remainTime = productPeriod.getMonth();
		}
		// DB에서 기간별 필터링
		int totalCount = depositMapper.countDepositsWithFilters(spclConditions, remainTime);
		List<Deposit> deposits = depositMapper.selectDepositsWithFilters(spclConditions, remainTime,
			(int)pageable.getOffset(), pageable.getPageSize()
		);
		// 상품마다 금융회사 정보도 가져오기
		List<DepositsResponse> depositsResponses = deposits.stream()
			.map(this::convertToDepositsResponse)
			.toList();
		return new PageImpl<>(depositsResponses, pageable, totalCount);
	}

	// 예금 상품 상세 정보 조회
	@Override
	@Transactional(readOnly = true)
	public DepositsResponse getDepositDetail(Long depositId) {
		Deposit deposit = depositMapper.selectProductDetailById(depositId);
		if (deposit == null) {
			throw new CustomException(FinancialErrorCode.DEPOSIT_SAVING_NOT_FOUND,null,null,null);
		}
		FinancialCompany company = financialCompanyService.getById(deposit.getFinancialCompanyId());
		List<DepositSpclCondition> depositConditions =
			depositMapper.selectSpclConditionsByDepositId(depositId);
		return DepositsResponse.of(deposit,company,depositConditions);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DepositsResponse> getAllDeposits(Pageable pageable) {

		int totalCount = depositMapper.countAllDeposits();

		List<Deposit> deposits = depositMapper.selectAllDeposits(
			(int)pageable.getOffset(),
			pageable.getPageSize()
		);
		List<DepositsResponse> depositsResponses = deposits.stream()
			.map(this::convertToDepositsResponse)
			.toList();
		return new PageImpl<>(depositsResponses, pageable, totalCount);
	}

	private Map<String, FinancialCompany> createCompanyMap() {
		List<FinancialCompany> companies = financialCompanyMapper.selectAllFinancialCompany();
		Map<String, FinancialCompany> companyMap = new HashMap<>();
		companies.forEach(company -> companyMap.put(company.getCode(), company));
		return companyMap;
	}

	private List<Deposit> collectDepositData(Map<String, FinancialCompany> companyMap) {
		List<Deposit> deposits = new ArrayList<>();
		Set<String> processedProducts = new HashSet<>(); // 중복방지

		// 기간별 데이터 수집
		List<Integer> periods = List.of(
			ProductPeriod.SIX_MONTH.getMonth(),
			ProductPeriod.ONE_YEAR.getMonth(),
			ProductPeriod.TWO_YEAR.getMonth()
		);
		for (Integer period : periods) {
			collectProductsByPeriod(period, companyMap, deposits, processedProducts);
		}
		log.info("전체 상품 수집 완료: {}개", deposits.size());
		return deposits;
	}

	private int collectProductsByPeriod(int period, Map<String, FinancialCompany> companyMap,
		List<Deposit> deposits, Set<String> processedProducts) {

		int offset = 0;
		int periodProductCount = 0;
		int consecutiveEmptyCount = 0;
		final int MAX_EMPTY_RESPONSES = 3;

		while (consecutiveEmptyCount < MAX_EMPTY_RESPONSES) {
			List<DepositProduct> products = null;
			try {
				products = apiHelper.getProductsByPeriod(period, offset);
			} catch (Exception e) {
				log.warn("{}개월 API 호출 실패 (offset: {}): {}", period, offset, e.getMessage());
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
				String productKey = product.companyCode() + "_" + product.code() + "_" + period;
				// 중복 체크
				if (processedProducts.contains(productKey)) {
					continue;
				}
				// 유효성 체크
				if (!dataHelper.isValidProduct(product, companyMap)) {
					continue;
				}
				processedProducts.add(productKey);
				FinancialCompany company = companyMap.get(product.companyCode());
				Deposit deposit = dataHelper.convertToDepositEntity(product, period, company);
				deposits.add(deposit);
				periodProductCount++;
			}
			offset += 20;
		}
		return periodProductCount;
	}

	private List<DepositSpclCondition> collectSpclConditionData(List<Deposit> savedDeposits) {
		List<DepositSpclCondition> spclConditions = new ArrayList<>();
		log.info("🔍 총 {}개 상품의 우대조건 수집 시작", savedDeposits.size());

		for (Deposit deposit : savedDeposits) {
			try {
				log.info("🔍 상품 {} 우대조건 수집 시도", deposit.getProductCode());
				DepositApiHelper.ProductDetailInfo detailInfo = apiHelper.getProductDetailInfo(
					deposit.getProductCode());
				log.info("🔍 상품 {} API 호출 성공 - 우대조건 {}개",
					deposit.getProductCode(), detailInfo.specialConditions().size());
				detailInfo.specialConditions().stream()
					.map(conditionName -> {
						// 4. 개별 우대조건 변환 시도
						log.info("🔍 우대조건 변환 시도: {}", conditionName);
						DepositSpclCondition condition = dataHelper.convertToSpclCondition(conditionName, deposit);

						// 5. 변환 성공/실패 결과
						if (condition != null) {
							log.info("✅ 우대조건 변환 성공: {} -> {}", conditionName, condition.getSpclCondition());
						} else {
							log.warn("❌ 우대조건 변환 실패: {}", conditionName);
						}
						return condition;
					})
					.filter(Objects::nonNull)
					.forEach(spclConditions::add);
			} catch (Exception e) {
				log.warn("상품 {}의 우대조건 수집 실패: {}", deposit.getProductCode(), e.getMessage());
				continue;
			}
			// 	detailInfo.specialConditions().stream()
			// 		.map(conditionName -> dataHelper.convertToSpclCondition(conditionName, deposit))
			// 		.filter(Objects::nonNull)
			// 		.forEach(spclConditions::add);
			// } catch (Exception e) {
			// 	log.warn("상품 {}의 우대조건 수집 실패: {}", deposit.getProductCode(), e.getMessage());
			// 	continue;
			// }
		}
		log.info("우대조건 수집 완료 : {}", spclConditions.size());
		return spclConditions;
	}

	private List<Deposit> saveDepositsToDatabase(List<Deposit> deposits) {
		if (deposits.isEmpty())
			return Collections.emptyList();
		log.info("DB 저장 시작: {}개 상품", deposits.size());
		// 배치 저장
		depositMapper.insertBatch(deposits);

		return deposits.stream()
			.map(deposit -> depositMapper.selectByProductAndCompanyCode(
				deposit.getProductCode(), deposit.getCompanyCode(), deposit.getSaveMonth()))
			.filter(Objects::nonNull)
			.toList();
	}

	private void saveSpclConditionsToDatabase(List<DepositSpclCondition> spclConditions,
		Map<String, Long> depositIdMap) {
		if (spclConditions.isEmpty())
			return;
		List<DepositSpclCondition> validConditions = spclConditions.stream()
			.filter(condition -> condition.getSpclCondition() != null)  // null 제거
			.toList();
		if (validConditions.isEmpty()) {
			log.warn("저장할 유효한 우대조건이 없습니다.");
			return;
		}

		List<DepositSpclCondition> conditionWithDepositId =
			dataHelper.mapSpclConditionsWithDepositId(validConditions, depositIdMap);

		if (!conditionWithDepositId.isEmpty()) {
			depositMapper.insertSpclCondition(conditionWithDepositId);
		}
	}

	private DepositsResponse convertToDepositsResponse(Deposit deposit) {
		FinancialCompany company = financialCompanyService.getById(deposit.getFinancialCompanyId());

		List<DepositSpclCondition> depositConditions =
			depositMapper.selectSpclConditionsByDepositId(deposit.getDepositId());

		return DepositsResponse.of(deposit, company, depositConditions);
	}
}
