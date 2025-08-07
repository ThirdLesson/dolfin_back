package org.scoula.domain.financialproduct.depositsaving.service;

import java.util.List;

import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.constants.SavingSpclConditionType;
import org.scoula.domain.financialproduct.depositsaving.dto.response.DepositsResponse;
import org.scoula.domain.financialproduct.depositsaving.dto.response.SavingsResponse;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.domain.financialproduct.depositsaving.entity.Saving;
import org.scoula.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavingService {

	// 적금상품 API를 호출해서 가져와 저장
	List<Saving> fetchAndSaveSavings();

	// 우대조건 정보만 저장 - 기존에 저장된 적금 상품들과 연결
	void fetchAndSaveSpclConditions(List<Saving> savedSavings);

	// // 적금 상품 리스트 조회(필터링+페이징)
	Page<SavingsResponse> getSavings(ProductPeriod productPeriod, List<SavingSpclConditionType> spclConditions,
		Pageable pageable, Member member);

	// 	적금 상품 상세 정보 조회
	SavingsResponse getSavingDetail(Long savingId);

	// 	전체 조회용(체류기간 필터링 없음)
	Page<SavingsResponse> getAllSavings(Pageable pageable);
}

