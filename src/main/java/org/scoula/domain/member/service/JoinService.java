package org.scoula.domain.member.service;

import org.scoula.domain.member.dto.request.JoinRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JoinService {

	void joinMember(JoinRequest joinRequest) throws JsonProcessingException;

	void checkLoginId(String loginId);
}
