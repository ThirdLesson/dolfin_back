package org.scoula.domain.member.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scoula.domain.member.dto.request.ReissueTokenRequestDto;
import org.scoula.domain.member.dto.request.SignInRequestDto;
import org.scoula.domain.member.dto.response.RefreshResponseDto;
import org.scoula.domain.member.dto.response.SignInResponseDto;

public interface MemberAuthService {
	SignInResponseDto signIn(SignInRequestDto signInRequestDto, HttpServletRequest request,
		HttpServletResponse response);

	void signOut(HttpServletResponse response);

	public RefreshResponseDto reissueToken(ReissueTokenRequestDto reissueTokenRequestDto, String refreshToken,
		HttpServletRequest request);
}
