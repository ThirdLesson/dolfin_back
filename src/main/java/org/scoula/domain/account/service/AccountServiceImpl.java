package org.scoula.domain.account.service;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.account.dto.request.CreateAccountRequest;
import org.scoula.domain.account.entity.Account;
import org.scoula.domain.account.exception.AccountErrorCode;
import org.scoula.domain.account.mapper.AccountMapper;
import org.scoula.domain.member.dto.MemberDTO;
import org.scoula.domain.member.exception.MemberErrorCode;
import org.scoula.domain.member.service.MemberService;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

	private final AccountMapper accountMapper;
	private final MemberService memberService;

	@Override
	public void createAccount(CreateAccountRequest createAccountRequest, HttpServletRequest request) {
		MemberDTO memberById = Optional.ofNullable(memberService.getMemberById(createAccountRequest.getMemberId()))
			.orElseThrow(() -> new CustomException(
				MemberErrorCode.MEMBER_NOT_FOUND,
				LogLevel.WARNING,
				"회원 정보를 찾을 수 없습니다.",
				Common.builder().build()
			));
		Account byAccountNumber = accountMapper.findByAccountNumber(createAccountRequest.getAccountNumber());
		if (byAccountNumber != null) {
			throw new CustomException(AccountErrorCode.EXIST_ACCOUNT_NUMBER, LogLevel.WARNING, null,
				Common.builder().deviceInfo(request.getHeader("host-agent")).srcIp(request.getRemoteAddr())
					.callApiPath(request.getRequestURI()).apiMethod(request.getMethod()).memberId(
						String.valueOf(createAccountRequest.getMemberId())).build());
		}
		accountMapper.createAccount(createAccountRequest);
	}
}
