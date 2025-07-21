package org.scoula.domain.member.service;

import org.scoula.domain.member.dto.request.JoinRequest;

public interface JoinService {

	void joinMember(JoinRequest joinRequest);

	void checkLoginId(String loginId);
}
