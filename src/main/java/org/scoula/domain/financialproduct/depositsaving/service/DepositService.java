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

	List<Deposit> fetchAndSaveDeposits();

	void fetchAndSaveSpclConditions(List<Deposit> savedDeposits);

	Page<DepositsResponse> getDeposits(ProductPeriod productPeriod, List<DepositSpclConditionType> spclConditions,
		Pageable pageable, Member member);

	DepositsResponse getDepositDetail(Long depositId);

	Page<DepositsResponse> getAllDeposits(Pageable pageable);
}
