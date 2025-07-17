package org.scoula.domain.financialCompany.jeonseLoan.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JeonseLoan {
    private Long jeonseLoanId;          // 전세자금대출 아이디
    private Long financialCompanyId;    // 금융회사 아이디
    private String name;       // 금융상품명
    private String joinWay;             // 가입 방법
    private String loanExpensive;       // 대출 부대비용
    private String erlyFee;             // 중도상환 수수료
    private BigDecimal dlyRate;         // 연체 이자율
    private String loanLmt;             // 대출 한도
    private Long jeonseId;              // 전세대출 기본정보
    private String repayTypeName;       // 대출 상환 유형명
    private String lendRateTypeName;    // 대출 금리 유형명
    private BigDecimal lendRateMin;     // 대출금리최저
    private BigDecimal lendRateMax;     // 대출금리최고
    private BigDecimal lendRateAvg;     // 전체 취급 평균금리
}
