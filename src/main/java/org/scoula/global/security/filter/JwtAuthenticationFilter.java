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
		String token = resolveToken(request);

		if (token == null) {
			request.setAttribute("notExistAccessToken", 401);
			customAuthenticationEntryPoint.commence(request, response,
				new JwtAuthenticationException("notExistAccessToken"));
			return;
		}

		try {
			if (jwtTokenProvider.validateToken(request, response, token)) {
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
		return requestURI.startsWith("/auth") ||
			requestURI.startsWith("/swagger") ||
			requestURI.startsWith("/v2") ||
			requestURI.startsWith("/webjars") ||
			requestURI.startsWith("/configuration");
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}
