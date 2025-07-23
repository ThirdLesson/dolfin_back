package org.scoula.domain.exchange.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 환율
 */
@Getter
@Setter  // MyBatis는 setter가 필요합니다!
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExchangeRate extends BaseEntity {

	private Long exchangeId;
	private String baseExchange; 		// 기준 통화
	private String targetExchange; 		// 대상 통화
	private BigDecimal exchangeValue; 	// 환율 값
	private String bankName; 			// 은행 이름
	private LocalDate ExchangeDate; 	// 환율 적용 날짜
	private Type type;                  // 환전 타입 / SELLCASH(현금 송금), GETCASH(현금 수취), SEND(송금), RECEIVE(수취), BASE(기준 환율)
}
