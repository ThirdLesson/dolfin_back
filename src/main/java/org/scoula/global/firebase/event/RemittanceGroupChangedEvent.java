package org.scoula.global.firebase.event;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RemittanceGroupChangedEvent {
	private final List<Long> memberIds;
}
