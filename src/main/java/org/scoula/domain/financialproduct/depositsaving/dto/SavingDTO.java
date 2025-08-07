package org.scoula.domain.financialproduct.depositsaving.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.scoula.domain.financialproduct.constants.SavingSpclConditionType;
import org.scoula.domain.financialproduct.depositsaving.entity.Saving;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingDTO {
	private Long savingId;
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
	private List<SavingSpclConditionType> spclConditions;

	// Entity → DTO
	public static SavingDTO fromEntity(Saving entity, List<SavingSpclConditionType> spclConditions) {
		return SavingDTO.builder()
			.savingId(entity.getSavingId())
			.financialCompanyId(entity.getFinancialCompanyId())
			.name(entity.getName())
			.saveMonth(entity.getSaveMonth())
			.interestRate(entity.getInterestRate())
			.maxInterestRate(entity.getMaxInterestRate())
			.productCode(entity.getProductCode())
			.companyCode(entity.getCompanyCode())
			.createdAt(entity.getCreatedAt())
			.updatedAt(entity.getUpdatedAt())
			.spclConditions(spclConditions)
			.build();
	}

	// 	DTO -> ENTITY
	public Saving toEntity() {
		return Saving.builder()
			.savingId(this.savingId)
			.financialCompanyId(this.financialCompanyId)
			.name(this.name)
			.saveMonth(this.saveMonth)
			.interestRate(this.interestRate)
			.maxInterestRate(this.maxInterestRate)
			.productCode(this.productCode)
			.companyCode(this.companyCode)
			.build();
	}
}
