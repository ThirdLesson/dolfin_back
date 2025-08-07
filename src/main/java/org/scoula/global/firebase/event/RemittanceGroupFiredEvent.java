package org.scoula.global.firebase.event;

import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RemittanceGroupFiredEvent {
	private final MemberWithInformationDto member;
}
