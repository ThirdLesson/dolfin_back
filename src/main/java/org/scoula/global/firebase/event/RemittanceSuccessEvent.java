package org.scoula.global.firebase.event;

import java.math.BigDecimal;

import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;
import org.scoula.global.constants.Currency;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RemittanceSuccessEvent {
	private final MemberWithInformationDto member;
	private final BigDecimal sendAmount;
	private final Currency currency;
}
