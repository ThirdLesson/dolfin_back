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
	private String name;                           // 상품명
	private String joinWay;                        // 가입방법
	private String loanExpensive;                  // 대출 부대비용
	private String erlyFee;                        // 중도상환수수료
	private String dlyRate;                        // 연체이자율
	private String loanLmt;                        // 대출한도
	private String repayTypeName;                  // 상환유형명
	private String lendRateTypeName;               // 대출금리유형명
	private BigDecimal lendRateMin;                // 최저금리
	private BigDecimal lendRateMax;                // 최고금리
	private BigDecimal lendRateAvg;                // 평균금리
	private Integer minRemainingVisaMonths;        // 최소 필요 잔여 체류기간(개월)
	private Integer maxLoanTermMonths;             // 최대 대출기간(개월)
	private Long loanMinAmount;                    // 최소 대출금액(원)
	private Long loanMaxAmount;                    // 최대 대출금액(원)
}
