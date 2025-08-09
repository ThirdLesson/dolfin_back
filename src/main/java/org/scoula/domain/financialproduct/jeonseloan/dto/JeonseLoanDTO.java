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

	// 회사/상품
	private Long financialCompanyId;
	private String productName;

	// 금리 정보
	private BigDecimal minRate;
	private BigDecimal maxRate;
	private BigDecimal avgRate;

	// 한도/기간/상환
	private Long minLoanAmount;
	private Long maxLoanAmount;
	private Integer minPeriod;
	private Integer maxPeriod;

	// 가입/조건
	private Boolean joinAvailable;
	private Boolean foreignerAvailable;
	private Boolean isActive;
	private String loanConditions;
	private String rateInfo;


	public static JeonseLoanDTO from(JeonseLoan entity,Boolean joinAvailable) {
		return JeonseLoanDTO.builder()
			.jeonseLoanId(entity.getJeonseLoanId())
			.financialCompanyId(entity.getFinancialCompanyId())
			.productName(entity.getProductName())
			.minRate(entity.getBaseRate())
			.maxRate(entity.getMaxRate())
			.avgRate(entity.getAvgRate())
			.minLoanAmount(entity.getMinLoanAmount())
			.maxLoanAmount(entity.getMaxLoanAmount())
			.minPeriod(entity.getMinPeriodMonths())
			.maxPeriod(entity.getMaxPeriodMonths())
			.joinAvailable(joinAvailable)
			.foreignerAvailable(entity.getForeignerAvailable())
			.isActive(entity.getIsActive())
			.loanConditions(entity.getLoanConditions())
			.rateInfo(entity.getRateInfo())
			.build();
	}
}
