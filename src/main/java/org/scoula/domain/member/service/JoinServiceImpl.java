package org.scoula.domain.member.service;

import static org.scoula.domain.member.exception.MemberErrorCode.*;
import static org.scoula.global.constants.Currency.*;

import org.scoula.domain.member.dto.request.JoinRequest;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.member.mapper.MemberMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class JoinServiceImpl implements JoinService {

	private final MemberMapper memberMapper;

	@Override
	public void joinMember(JoinRequest joinRequest) {
		checkLoginId(joinRequest.loginId());

		Member member = Member.builder()
			.loginId(joinRequest.loginId())
			.password(joinRequest.password())
			.passportNumber(joinRequest.passportNumber())
			.name(joinRequest.name())
			.birth(joinRequest.birth())
			.nationality(joinRequest.nationality())
			.phoneNumber(joinRequest.phoneNumber())
			.currency(USD)
			.remainTime(joinRequest.remainTime())
			.build();

		memberMapper.insertMember(member);
	}

	@Override
	public void checkLoginId(String loginId) {
		if (checkLoginIdDuplicate(loginId)) {
			throw new CustomException(LOGIN_ID_ALREADY_EXISTS, LogLevel.INFO, null, Common.builder().build());
		}
	}

	private boolean checkLoginIdDuplicate(String loginId) {
		return memberMapper.checkLoginIdDuplicate(loginId) > 0;
	}

}
