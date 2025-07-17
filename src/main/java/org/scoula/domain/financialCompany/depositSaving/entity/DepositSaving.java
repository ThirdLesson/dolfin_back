package org.scoula.domain.financialCompany.depositSaving.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositSaving  {
    private Long depositSavingId; // 아이디
    private Long financialCompanyId; // FK
    private DepositSavingType type;       
    private String name;          // 금융 상품명
    private String joinWay;       // 가입 방법
    private String interestDescription;  // 만기 후 이자율 설명
    private String spdCondition;  // 우대조건
    private String etcNote;       // 기타 유의사항
    private String maxLimit;      // 최고 한도
    private Integer saveMonth;    // 저축 기간(개월)
    private BigDecimal interestRate;     // 기본 금리
    private BigDecimal maxInterestRate;  // 우대 최고 금리
    private String interestRateType;     // 금리 유형
    private String reserveType;          // 적립 유형
}
