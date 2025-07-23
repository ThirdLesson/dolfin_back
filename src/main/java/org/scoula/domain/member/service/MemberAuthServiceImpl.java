package org.scoula.domain.member.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scoula.domain.member.dto.MemberDTO;
import org.scoula.domain.member.dto.request.ReissueTokenRequestDto;
import org.scoula.domain.member.dto.request.SignInRequestDto;
import org.scoula.domain.member.dto.response.RefreshResponseDto;
import org.scoula.domain.member.dto.response.SignInResponseDto;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.redis.util.RedisUtil;
import org.scoula.global.security.dto.JwtToken;
import org.scoula.global.security.exception.JwtErrorMessage;
import org.scoula.global.security.util.CookieUtil;
import org.scoula.global.security.util.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberAuthServiceImpl implements MemberAuthService {

	private final MemberService memberService;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisUtil redisUtil;

	@Override
	public SignInResponseDto signIn(SignInRequestDto signInRequestDto, HttpServletRequest request,
		HttpServletResponse response) {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			signInRequestDto.loginId(),
			signInRequestDto.password());

		authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// authenticate 메서드 내부에서 이미 존재하는지 검증이 되기 때문에 한 번 더 검증을 해주지 않았음
		// authenticate 에서 조회한 멤버 정보를 가져올 방법을 생각해 쿼리를 한 번 줄이면 좋을 듯
		MemberDTO member = memberService.getMemberByLoginId(signInRequestDto.loginId());

		JwtToken jwtToken = jwtTokenProvider.generateToken(member.getMemberId(), member.getLoginId());
		CookieUtil.setCookie(response, "refreshToken", jwtToken.getRefreshToken());

		return SignInResponseDto.from(jwtToken, member);

	}

	@Override
	public void signOut(HttpServletResponse response) {
		CookieUtil.deleteCookie(response, "refreshToken");
	}

	@Override
	public RefreshResponseDto reissueToken(ReissueTokenRequestDto reissueTokenRequestDto, String refreshToken,
		HttpServletRequest request, HttpServletResponse response) {
		String refreshTokenByRedis = redisUtil.get(reissueTokenRequestDto.loginId());
		log.info("레디스 저장 토큰: {}", refreshTokenByRedis);
		log.info("사용자 토큰: {}", refreshToken);
		if (!refreshTokenByRedis.equals(refreshToken)) {
			throw new CustomException(JwtErrorMessage.NOT_MATCH_REFRESH_TOKEN, LogLevel.WARNING,
				request.getHeader("txId"), Common.builder()
				.srcIp(request.getRemoteAddr())
				.callApiPath(request.getRequestURI())
				.deviceInfo(request.getHeader("user-agent"))
				.retryCount(0)
				.build());
		}

		JwtToken jwtToken = jwtTokenProvider.generateToken(reissueTokenRequestDto.memberId(),
			reissueTokenRequestDto.loginId());
		CookieUtil.setCookie(response, "refreshToken", jwtToken.getRefreshToken());
		return new RefreshResponseDto(jwtToken.getAccessToken());
	}

}
