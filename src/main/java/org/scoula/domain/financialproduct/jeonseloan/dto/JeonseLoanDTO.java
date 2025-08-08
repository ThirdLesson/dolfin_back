package org.scoula.domain.financialproduct.jeonseloan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class JeonseLoanDTO {
	private Long jeonseLoanId;
	private Long financialCompanyId;
	private String name;
	private String joinWay;
	private String loanExpensive;
	private String erlyFee;
	private String dlyRate;
	private String loanLmt;
	private String repayTypeName;
	private String lendRateTypeName;
	private BigDecimal lendRateMin;
	private BigDecimal lendRateMax;
	private BigDecimal lendRateAvg;
	private Integer minRemainingVisaMonths;
	private Integer maxLoanTermMonths;
	private Long loanMinAmount;
	private Long loanMaxAmount;
	private Boolean joinAvailable;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	public static JeonseLoanDTO from(JeonseLoan entity,Boolean joinAvailable) {
		return JeonseLoanDTO.builder()
			.jeonseLoanId(entity.getJeonseLoanId())
			.financialCompanyId(entity.getFinancialCompanyId())
			.name(entity.getName())
			.joinWay(entity.getJoinWay())
			.loanExpensive(entity.getLoanExpensive())
			.erlyFee(entity.getErlyFee())
			.dlyRate(entity.getDlyRate())
			.loanLmt(entity.getLoanLmt())
			.repayTypeName(entity.getRepayTypeName())
			.lendRateTypeName(entity.getLendRateTypeName())
			.lendRateMin(entity.getLendRateMin())
			.lendRateMax(entity.getLendRateMax())
			.lendRateAvg(entity.getLendRateAvg())
			.minRemainingVisaMonths(entity.getMinRemainingVisaMonths())
			.maxLoanTermMonths(entity.getMaxLoanTermMonths())
			.loanMinAmount(entity.getLoanMinAmount())
			.loanMaxAmount(entity.getLoanMaxAmount())
			.joinAvailable(joinAvailable)
			.createdAt(entity.getCreatedAt())
			.updatedAt(entity.getUpdatedAt())
			.build();
	}
}
