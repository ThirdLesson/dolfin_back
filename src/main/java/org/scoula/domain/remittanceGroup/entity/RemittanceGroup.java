package org.scoula.domain.remittanceGroup.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.scoula.global.entity.BaseEntity;

/**
 * 단체 송금
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemittanceGroup extends BaseEntity {

	private Long remittanceGroupId;         // 송금 그룹 아이디 (PK)
	private String title;                   // 제목
	private String currency;                // 통화
	private LocalDate date;                 // 송금 날짜
	private Integer count;                  // 인원
	private BigDecimal totalAmount;         // 총 금액
	private BigDecimal appliedExchangeRate; // 적용 환율
}
