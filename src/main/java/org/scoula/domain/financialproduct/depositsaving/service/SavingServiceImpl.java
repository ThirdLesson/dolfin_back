package org.scoula.domain.financialproduct.depositsaving.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.constants.SavingSpclCondition;
import org.scoula.domain.financialproduct.depositsaving.dto.response.SavingsResponse;
import org.scoula.domain.financialproduct.depositsaving.entity.Saving;
import org.scoula.domain.financialproduct.depositsaving.mapper.SavingMapper;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.financialproduct.financialcompany.service.FinancialCompanyService;
import org.scoula.domain.member.entity.Member;
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
public class SavingServiceImpl implements SavingService{

	private final SavingMapper savingMapper;
	private final FinancialCompanyService financialCompanyService;

	@Override
	@Transactional
	public List<SavingsResponse> fetchAndSaveSavingProducts () {
		return null;
	}
	// 적금 상품 리스트 조회(필터링)
	@Override
	public Page<SavingsResponse> getSavings(ProductPeriod productPeriod, List<SavingSpclCondition> spclConditions,
		Pageable pageable, Member member) {
		// // 멤버 체류기간 가져오기
		// LocalDate remainTime = member.getRemainTime();
		// int remainMonths = (int)ChronoUnit.MONTHS.between(LocalDate.now(), remainTime);
		//
		// int totalCount = savingMapper.countSavingWithFilters(
		// 	productPeriod, spclConditions, remainTime, remainMonths, pageable.getPageSize());
		//
		// // 기간별,조건별 필터링, pageable
		// List<Saving> savings = savingMapper.selectSavingWithFilters(
		// 	productPeriod, spclConditions, remainTime, remainMonths,
		// 	(int)pageable.getOffset(), pageable.getPageSize()
		// );
		//
		// List<SavingsResponse> savingsResponses = savings.stream()
		// 	.map(saving -> SavingsResponse.fromEntity(saving,
		// 		financialCompanyService.getById(saving.getFinancialCompanyId())))
		// 	.toList();
		// return new PageImpl<>(savingsResponses, pageable, totalCount);
		return null;
	}

}
