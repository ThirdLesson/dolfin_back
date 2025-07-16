package org.scoula.domain.remittanceGroup.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemittanceGroup {
    
    private Long remittanceGroupId;      // 송금 그룹 아이디 (PK)
    private String title;                // 제목
    private String currency;             // 통화
    private Date date;                   // 송금 날짜
    private Integer count;               // 인원
    private Long totalAmount;            // 총 금액
    private BigDecimal appliedExchangeRate; // 적용 환율
}
