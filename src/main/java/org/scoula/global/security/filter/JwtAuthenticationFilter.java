package org.scoula.global.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scoula.global.security.exception.CustomAuthenticationEntryPoint;
import org.scoula.global.security.exception.JwtAuthenticationException;
import org.scoula.global.security.util.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		// 1. Request Header에서 JWT 토큰 추출
		String token = resolveToken(request);

		// 2. 새로고침 시 메모리에 저장된 액세스 토큰이 사라졌을 시 발생시키는 에러
		if (token == null) {
			request.setAttribute("notExistAccessToken", 401);
			customAuthenticationEntryPoint.commence(request, response,
				new JwtAuthenticationException("notExistAccessToken"));
			return;
		}

		// 3. validateToken으로 토큰 유효성 검사
		try {
			if (jwtTokenProvider.validateToken(request, response, token)) {
				// 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
				Authentication authentication = jwtTokenProvider.getAuthentication(request, response, token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (JwtAuthenticationException e) {
			customAuthenticationEntryPoint.commence(request, response, e);
			return;
		}
		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		// 해당 토큰 검증 필터가 적용되지 않게 하려는 api 경로
		return requestURI.startsWith("/auth") ||
			requestURI.startsWith("/swagger") ||
			requestURI.startsWith("/v2") ||
			requestURI.startsWith("/webjars") ||
			requestURI.startsWith("/configuration");
	}

	// Request Header에서 토큰 정보 추출
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}
