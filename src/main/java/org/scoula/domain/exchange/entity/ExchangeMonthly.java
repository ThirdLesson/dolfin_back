package org.scoula.domain.exchange.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.scoula.global.entity.BaseEntity;
import org.springframework.data.annotation.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeMonthly extends BaseEntity {

	@Id
	private String exchangeId;

	private String targetExchange; 
	private BigDecimal exchangeValue; 
	private LocalDate exchangeDate; 

}
