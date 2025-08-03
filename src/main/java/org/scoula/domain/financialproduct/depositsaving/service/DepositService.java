package org.scoula.domain.financialproduct.depositsaving.service;

import java.util.List;

import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.depositsaving.dto.response.DepositsResponse;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepositService {

	// 예금 상품 정보만 저장
	List<Deposit> fetchAndSaveDeposits();

	// 우대조건 정보만 저장 - 기존에 저장된 예금 상품들과 연결
	void fetchAndSaveSpclConditions(List<Deposit> savedDeposits);

	// 예금 상품 리스트 조회(필터링 + 페이징)
	Page<DepositsResponse> getDeposits(ProductPeriod productPeriod, List<DepositSpclConditionType> spclConditions,
		Pageable pageable, Member member);

	// 	에금 상품 상세 정보 조회
	DepositsResponse getDepositDetail(Long depositId);

	// 전체 조회용 (체류기간 필터링 없음)
	Page<DepositsResponse> getAllDeposits(Pageable pageable);
}
