package org.scoula.domain.member.service;

import static org.scoula.domain.member.exception.MemberErrorCode.*;
import static org.scoula.global.constants.Currency.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.codef.dto.request.StayExpirationRequest;
import org.scoula.domain.codef.dto.response.StayExpirationResponse;
import org.scoula.domain.codef.service.CodefApiClient;
import org.scoula.domain.member.dto.request.JoinRequest;
import org.scoula.domain.member.dto.response.JoinResponse;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.member.mapper.MemberMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class JoinServiceImpl implements JoinService {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter STAY_EXPIRATION_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
	private final MemberMapper memberMapper;
	private final CodefApiClient codefApiClient;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	@Override
	public JoinResponse joinMember(JoinRequest joinRequest, HttpServletRequest request) throws JsonProcessingException {
		checkLoginId(joinRequest.loginId());

		LocalDate date = LocalDate.parse(joinRequest.birth(), DATE_FORMATTER);
		String StayExpirationBirthDate = date.format(STAY_EXPIRATION_FORMATTER);

		StayExpirationRequest stayExpirationRequest = StayExpirationRequest.builder()
			.organization("0001") // 고정값 0001
			.birthDate(StayExpirationBirthDate)
			.passportNo(joinRequest.passportNumber())
			.nationality(joinRequest.nationality().getCode())
			.country(joinRequest.country())
			.build();
		LocalDate stayExpiration = getStayExpiration(stayExpirationRequest, request);

		Member member = Member.builder()
			.loginId(joinRequest.loginId())
			.password(encodePassword(joinRequest.password()))
			.passportNumber(joinRequest.passportNumber())
			.name(joinRequest.name())
			.birth(LocalDate.parse(joinRequest.birth(), DATE_FORMATTER))
			.nationality(joinRequest.nationality())
			.phoneNumber(joinRequest.phoneNumber())
			.currency(USD) // default USD
			.remainTime(stayExpiration)
			.build();

		memberMapper.insertMember(member);

		return JoinResponse.of(member);
	}

	@Override
	public void checkLoginId(String loginId) {
		if (checkLoginIdDuplicate(loginId)) {
			throw new CustomException(LOGIN_ID_ALREADY_EXISTS, LogLevel.INFO, null, Common.builder().build());
		}
	}

	private LocalDate getStayExpiration(StayExpirationRequest stayExpirationRequest, HttpServletRequest request) throws
		JsonProcessingException {
		StayExpirationResponse stayExpirationResponse = codefApiClient.getStayExpiration(stayExpirationRequest,
			request);
		String expirationDate = stayExpirationResponse.resExpirationDate();

		if (stayExpirationResponse.resAuthenticity().equals("0") && expirationDate == null) {
			// throw new CustomException(STAY_EXPIRATION, LogLevel.WARNING, null, null);
			return null;
		}
		return LocalDate.parse(expirationDate, DATE_FORMATTER);
	}

	private boolean checkLoginIdDuplicate(String loginId) {
		return memberMapper.checkLoginIdDuplicate(loginId) > 0;
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

}
