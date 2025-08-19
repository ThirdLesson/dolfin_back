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


@Getter
@Setter  
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExchangeRate extends BaseEntity {

	private Long exchangeId;
	private String baseExchange; 		
	private String targetExchange; 		
	private BigDecimal exchangeValue; 	
	private String bankName; 			
	private LocalDate ExchangeDate; 	
	private Type type;                  
}
