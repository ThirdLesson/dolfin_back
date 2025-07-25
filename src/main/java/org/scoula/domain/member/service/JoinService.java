package org.scoula.domain.member.service;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.member.dto.request.JoinRequest;
import org.scoula.domain.member.dto.response.JoinResponse;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface JoinService {

	JoinResponse joinMember(JoinRequest joinRequest, HttpServletRequest request) throws JsonProcessingException;

	void checkLoginId(String loginId);
}
