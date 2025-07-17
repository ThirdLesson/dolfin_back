package org.scoula.domain.financialproduct.personalcreditLoan.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 개인신용대출
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalCreditLoan {

    private Long personalCreditLoanId;   // 개인신용대출 아이디 (PK)
    private Long financialCompanyId;     // 금융회사 아이디 (FK)
    private String name;                 // 금융상품명
    private String joinWay;              // 가입 방법
    private String crdtPrdtTypeNm;       // 대출상품유형
    private String cbName;               // CB회사명

    private BigDecimal crdtGrad1;        // 900점 초과
    private BigDecimal crdtGrad4;        // 801~900점
    private BigDecimal crdtGrad5;        // 701~800점
    private BigDecimal crdtGrad6;        // 601~700점
    private BigDecimal crdtGrad10;       // 501~600점
    private BigDecimal crdtGrad11;       // 401~500점
    private BigDecimal crdtGrad12;       // 301~400점
    private BigDecimal crdtGrad13;       // 300점 이하
    private BigDecimal crdtGradAvg;      // 평균금리
}
