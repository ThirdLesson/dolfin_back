package org.scoula.domain.financialproduct.personalcreditloan.entity;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalCreditLoan extends BaseEntity {

	private Long personalLoanId;          // (PK) 아이디
	private Long financialCompanyId;      // (FK) 은행 id
	private String productName;           // 상품명
	private BigDecimal baseRate;          // 최저금리
	private BigDecimal maxRate;           // 최고금리
	private BigDecimal spreadRate;        // 가산금리(평균)
	private BigDecimal maxLoanAmount;           // 최대 대출 한도
	private Integer minPeriodMonths;      // 최소 대출 기간(개월)
	private Integer maxPeriodMonths;      // 최대 대출 기간(개월)
	private String rateInfo;              // 금리 정보
	private String loanConditions;        // 대출 조건
	private Boolean foreignerAvailable;   // 외국인 가능 여부
	private Integer visaMinMonths;        // 비자 소지 최소 기간(개월)
	private Boolean isActive;             // 활성화 여부

	// JOIN 결과 필드
	private String companyName;           // financial_company.name
	private String companyCode;           // financial_company.code

	private String companyCallNumber;  // 추가
	private String companyHomeUrl;      // 추가
}
