package org.scoula.domain.financialproduct.depositsaving.dto;

import lombok.*;

import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositDTO {
	private Long depositId;
	private Long financialCompanyId;
	private String name;
	private Integer saveMonth;
	private BigDecimal interestRate;
	private BigDecimal maxInterestRate;
	private String productCode;
	private String companyCode;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;
	private List<DepositSpclConditionType> spclConditions;

	public Deposit toEntity() {
		return Deposit.builder()
			.depositId(this.depositId)
			.financialCompanyId(this.financialCompanyId)
			.name(this.name)
			.saveMonth(this.saveMonth)
			.interestRate(this.interestRate)
			.maxInterestRate(this.maxInterestRate)
			.productCode(this.productCode)
			.companyCode(this.companyCode)
			.build();
	}

	public static DepositDTO fromEntity(Deposit entity, List<DepositSpclConditionType> spclConditions) {
		return DepositDTO.builder()
			.depositId(entity.getDepositId())
			.financialCompanyId(entity.getFinancialCompanyId())
			.name(entity.getName())
			.saveMonth(entity.getSaveMonth())
			.interestRate(entity.getInterestRate())
			.maxInterestRate(entity.getMaxInterestRate())
			.spclConditions(spclConditions)
			.productCode(entity.getProductCode())
			.companyCode(entity.getCompanyCode())
			.createdAt(entity.getCreatedAt())
			.updatedAt(entity.getUpdatedAt())
			.build();
	}
}
