package org.scoula.domain.financialproduct.depositsaving.service;

import java.util.List;

import org.scoula.domain.financialproduct.constants.DepositSpclCondition;
import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.depositsaving.dto.response.DepositsResponse;
import org.scoula.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepositService {

	// 예금상품 API를 호출해서 가져와 저장
	void fetchAndSaveDepositSaving();

	// 예금 상품 리스트 조회(필터링)
	Page<DepositsResponse> getDeposits(ProductPeriod productPeriod, List<DepositSpclCondition> spclConditions,
		Pageable pageable, Member member);

}
