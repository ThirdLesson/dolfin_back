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

	List<Saving> fetchAndSaveSavings();

	void fetchAndSaveSpclConditions(List<Saving> savedSavings);

	Page<SavingsResponse> getSavings(ProductPeriod productPeriod, List<SavingSpclConditionType> spclConditions,
		Pageable pageable, Member member);

	SavingsResponse getSavingDetail(Long savingId);

	Page<SavingsResponse> getAllSavings(Pageable pageable);
}

