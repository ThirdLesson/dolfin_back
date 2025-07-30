package org.scoula.domain.financialproduct.depositsaving.dto;

import lombok.*;

import org.scoula.domain.financialproduct.constants.DepositSpclCondition;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.global.entity.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class DepositSavingDTO extends BaseEntity {
	private final Long depositSavingId;
	private final Long financialCompanyId;
	private final String name;
	private final String joinWay;
	private final String interestDescription;
	private final List<DepositSpclCondition> spclConditions;
	private final String ctcNote;
	private final String maxLimit;
	private final Integer saveMonth;
	private final BigDecimal interestRate;
	private final BigDecimal maxInterestRate;
	private final String interestRateType;
	private final String reserveType;

	// DTO → Entity
	public Deposit toEntity() {
		return Deposit.builder()
			.depositSavingId(this.depositSavingId)
			.financialCompanyId(this.financialCompanyId)
			.name(this.name)
			.spclCondition(this.spclConditions)
			.saveMonth(this.saveMonth)
			.interestRate(this.interestRate)
			.maxInterestRate(this.maxInterestRate)
			.build();
	}

	// Entity → DTO
	public static DepositSavingDTO fromEntity(Deposit entity) {
		return DepositSavingDTO.builder()
			.depositSavingId(entity.getDepositSavingId())
			.financialCompanyId(entity.getFinancialCompanyId())
			.name(entity.getName())
			.spclConditions(entity.getSpclCondition())
			.saveMonth(entity.getSaveMonth())
			.interestRate(entity.getInterestRate())
			.maxInterestRate(entity.getMaxInterestRate())
			.build();
	}
}
