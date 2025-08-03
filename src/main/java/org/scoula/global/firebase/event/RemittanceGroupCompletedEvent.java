package org.scoula.global.firebase.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RemittanceGroupCompletedEvent {
	private final Long remittanceGroupId;
}
