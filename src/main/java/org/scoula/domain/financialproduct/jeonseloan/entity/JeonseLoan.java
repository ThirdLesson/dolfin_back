package org.scoula.domain.financialproduct.jeonseloan.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

// 전세 대출 상품
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JeonseLoan extends BaseEntity {

	private Long jeonseLoanId;                     // 전세자금대출 ID
	private Long financialCompanyId;               // 금융회사 ID
	private String productName;                           // 상품명
	private String joinWay;

	private BigDecimal minRate;                   // 최저금리 (base_rate)
	private BigDecimal maxRate;                    // 최고금리 (max_rate)
	private BigDecimal avgRate;                    // 평균금리 (avg_rate)

	private Integer visaMinMonths;          // 최소 대출 기간(개월)
	private Integer maxPeriodMonths;             // 최대 대출기간(개월)
	private Integer minPeriodMonths;               // 최소 대출기간 (min_period_months)


	private Long minLoanAmount;                    // 최소 대출금액 (min_loan_amount)
	private Long maxLoanAmount;                    // 최대 대출금액 (max_loan_amount)

	private Boolean foreignerAvailable; // 외국인 가능 여부
	private Boolean isActive; //활성화 여부

	private String loanConditions; // 대출 가입 조건
	private String rateInfo; // 금리 정보 상세 설명
}
