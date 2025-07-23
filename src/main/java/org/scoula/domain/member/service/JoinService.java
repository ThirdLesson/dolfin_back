package org.scoula.domain.member.service;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.member.dto.request.JoinRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JoinService {

	void joinMember(JoinRequest joinRequest, HttpServletRequest request) throws JsonProcessingException;

	void checkLoginId(String loginId);
}
