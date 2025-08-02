package org.scoula.domain.exchange.entity;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ExchangePolicy 엔티티
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangePolicy extends BaseEntity {
    private Long policyId;                      // 정책 ID
    private String bankName;                    // 은행 이름
    private String targetCurrency;              // 목표 통화 코드 (예: VND, USD 등)
    private Type exchangeType;                  // 환전 타입 (예: SELLCASH, GETCASH 등)
    private String policyName;                  // 정책명
    private String description;                 // 정책 설명
    private BigDecimal exchangeDiscountRate;    // 환전 우대율
    private BigDecimal exchangeCommissionFee;   // 환전 수수료 우대율
    private BigDecimal baseTelegraphFee;        // 기본 전신환 수수료
    private Boolean isActive;                   // 정책 활성화 여부
}
