package org.scoula.domain.financialproduct.depositsaving.service;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.scoula.domain.financialproduct.constants.DepositSpclCondition;
import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.depositsaving.dto.common.ProductDetailResponse;
import org.scoula.domain.financialproduct.depositsaving.dto.common.ProductListResponse;
import org.scoula.domain.financialproduct.depositsaving.dto.response.DepositProduct;
import org.scoula.domain.financialproduct.depositsaving.dto.response.DepositsResponse;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.domain.financialproduct.depositsaving.mapper.DepositMapper;
import org.scoula.domain.financialproduct.exception.FinancialErrorCode;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.financialproduct.financialcompany.mapper.FinancialCompanyMapper;
import org.scoula.domain.financialproduct.financialcompany.service.FinancialCompanyService;
import org.scoula.domain.member.entity.Member;
import org.scoula.global.exception.CustomException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepositServiceImpl implements DepositService {

	private final DepositMapper depositMapper;
	private final FinancialCompanyService financialCompanyService;
	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;
	private final FinancialCompanyMapper financialCompanyMapper;

	private static final String DEPOSIT_PRODUCT_LIST_URL =
		"https://new-m.pay.naver.com/savings/api/v1/productList?productTypeCode=1002&companyGroupCode=BA&regionCode=00&productCategories%5B%5D=anyone&offset=%d&sortType=PRIME_INTEREST_RATE";
	private static final String DEPOSIT_DETAIL_URL =
		"https://new-m.pay.naver.com/savings/_next/data/Sxex5f02tUa16uzMlTx45/detail/%s.json?productCode=%s";

	// 예금 상품 리스트 조회(필터링)
	@Override
	public Page<DepositsResponse> getDeposits(ProductPeriod productPeriod, List<DepositSpclCondition> spclConditions,
		Pageable pageable, Member member) {
		// 멤버 체류기간 가져오기
		LocalDate remainTime = member.getRemainTime();
		int remainMonths = (int)ChronoUnit.MONTHS.between(LocalDate.now(), remainTime);

		int totalCount = depositMapper.countDepositsWithFilters(
			productPeriod, spclConditions, remainTime, remainMonths);
		// 기간별, 조건별 필터링,pageable
		List<Deposit> deposits = depositMapper.selectDepositsWithFilters(
			productPeriod, spclConditions, remainTime,
			(int)pageable.getOffset(), pageable.getPageSize()
		);
		// 상품마다 금융회사 정보도 가져오기
		List<DepositsResponse> depositsResponses = deposits.stream()
			.map(deposit -> DepositsResponse.fromEntity(deposit,
				financialCompanyService.getById(deposit.getFinancialCompanyId())))
			.toList();
		return new PageImpl<>(depositsResponses, pageable, totalCount);
	}

	@Override
	@Transactional
	public void fetchAndSaveDepositSaving() {
		try {
			// 금융회사 매핑 생성
			Map<String, FinancialCompany> companyMap = createCompanyMap();
			// 상품 목록 조회
			int offset = 0;
			List<DepositProduct> products = getProductAllFromNaver(offset);
			// 각 상품을 엔터티로 변환
			List<Deposit> deposits = new ArrayList<>();
			// List<Deposit> deposits = products.stream().map(product -> {
			for (DepositProduct product : products) {
				try {
					ProductDetailInfo detailInfo = getSpecialConditions(product.code());
					// 특별조건 조회
					// List<String> specialStrings = getSpecialConditions(product.code());
					List<DepositSpclCondition> specialEnums = detailInfo.specialConditions().stream()
						.map(DepositSpclCondition::fromValue)
						.filter(Objects::nonNull)
						.toList();

					FinancialCompany company = companyMap.get(product.companyCode());
					// product.companyCode()로 financialCompany 찾고 getFinancialId를 financialCompanyId에 넣어주기
					Deposit deposit = Deposit.builder()
						.financialCompanyId(company.getFinancialCompanyId())
						.name(product.name())
						.spclCondition(specialEnums)
						.saveMonth(detailInfo.savingTerm())
						.interestRate(new BigDecimal(product.interestRate()))
						.maxInterestRate(new BigDecimal(product.primeInterestRate()))
						.build();
					deposits.add(deposit);
				} catch (JsonProcessingException e) {
					log.error("JSON 파싱 실패 : {} - {}", product.name(), e.getMessage());
				} catch (RestClientException e) {
					log.error("API 호출 실패 : {} - {}", product.name(), e.getMessage());
				} catch (Exception e) {
					// 예외 로깅 또는 누적 처리
					log.warn("상품 처리 실패 : {} - {}", product.name(), e.getMessage());
				}
			}
			// DB에 저장 (예: batchInsert 사용)
			depositMapper.InsertBatch(deposits); // 또는 batchInsert()
		} catch (Exception e) {
			throw new CustomException(FinancialErrorCode.DEPOSIT_SAVING_BATCH_SAVE_FAILED, null, null, null);
		}
	}

	private ProductDetailInfo getSpecialConditions(String code) throws JsonProcessingException {
		String url = String.format(DEPOSIT_DETAIL_URL, code, code);
		String rawResponse = restTemplate.getForObject(url, String.class);

		ProductDetailResponse productDetailResponse = objectMapper.readValue(rawResponse, ProductDetailResponse.class);

		var result = productDetailResponse.pageProps()
			.dehydratedState()
			.queries()
			.stream()
			.filter(query -> query.queryKey().stream()
				.anyMatch(key -> "/productDetails".equals(key.url())))
			.findFirst()
			.map(query -> query.state().data().result())
			.orElse(null);

		if (result == null) {
			throw new CustomException(FinancialErrorCode.EXTERNAL_API_CALL_FAILED, null, null, null);
		}

		return new ProductDetailInfo(
			result.specialConditions(),
			result.savingTerm()
		);
	}

	private List<DepositProduct> getProductAllFromNaver(int offset) throws JsonProcessingException {
		String url = String.format(DEPOSIT_PRODUCT_LIST_URL, offset);
		String rawResponse = restTemplate.getForObject(url, String.class, offset);
		JavaType type = objectMapper.getTypeFactory()
			.constructParametricType(ProductListResponse.class, DepositProduct.class);

		ProductListResponse<DepositProduct> response = objectMapper.readValue(rawResponse, type);

		if (response.isSuccess()) {
			return response.result().products();
		} else {
			throw new CustomException(FinancialErrorCode.EXTERNAL_API_CALL_FAILED, null, null, null);
		}
	}

	private Map<String, FinancialCompany> createCompanyMap() {
		List<FinancialCompany> companies = financialCompanyMapper.selectAllFinancialCompany();
		Map<String, FinancialCompany> companyMap = new HashMap<>();

		for (FinancialCompany company : companies) {
			companyMap.put(company.getCode(), company);
		}

		log.info("금융회사 매핑 생성 완료: {}개", companyMap.size());
		return companyMap;
	}

	private record ProductDetailInfo(
		List<String> specialConditions,
		Integer savingTerm
	) {
	}
}


