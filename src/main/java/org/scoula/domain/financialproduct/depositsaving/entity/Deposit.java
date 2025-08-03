package org.scoula.domain.financialproduct.depositsaving.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class Deposit extends BaseEntity {
	private Long depositId;        // 아이디 (PK)
	private Long financialCompanyId;     // 금융회사 아이디 (FK)
	private String name;                 // 금융 상품명
	private Integer saveMonth;            // 저축 기간(개월)
	private BigDecimal interestRate;     // 기본 금리
	private BigDecimal maxInterestRate;  // 우대 최고 금리
	private String productCode;         // 상품코드 (조인키)
	private String companyCode;         // 회사코드 (조인키)
}
