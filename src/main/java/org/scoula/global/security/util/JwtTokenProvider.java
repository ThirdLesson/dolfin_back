package org.scoula.global.security.util;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scoula.domain.member.dto.MemberDTO;
import org.scoula.domain.member.service.MemberService;
import org.scoula.global.redis.util.RedisUtil;
import org.scoula.global.security.dto.CustomUserDetails;
import org.scoula.global.security.dto.JwtToken;
import org.scoula.global.security.exception.JwtAuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private static final long ACCESS_TOKEN_MAX_AGE = 1000L * 60 * 30;        
	private static final long REFRESH_TOKEN_MAX_AGE = 1000L * 60 * 60 * 24 * 3; 
	private static final int REDIS_REFRESH_TOKEN_MAX_AGE = 60 * 24 * 3; 

	private final Key key;
	private final MemberService memberService;
	private final RedisUtil redisUtil;

	public JwtTokenProvider(@Value("${JWT_SECRET}") String secretKey, MemberService memberService,
		RedisUtil redisUtil) {
		this.memberService = memberService;
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.redisUtil = redisUtil;
	}

	public JwtToken generateToken(Long memberId, String loginId) {

		long now = (new Date()).getTime();

		Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_MAX_AGE);
		String accessToken = Jwts.builder()
			.setSubject(String.valueOf(memberId))
			.claim("auth", "ROLE_" + "MEMBER")
			.setExpiration(accessTokenExpiresIn)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();

		String refreshToken = Jwts.builder()
			.setExpiration(new Date(now + REFRESH_TOKEN_MAX_AGE))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();

		redisUtil.set(loginId, refreshToken, REDIS_REFRESH_TOKEN_MAX_AGE);

		return JwtToken.builder()
			.grantType("Bearer")
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response,
		String accessToken) throws
		ServletException,
		IOException, JwtAuthenticationException {
		Claims claims = parseClaims(accessToken);

		if (claims.get("auth") == null) {
			request.setAttribute("cannotUseRefreshToken", 400);
			throw new JwtAuthenticationException("cannotUseRefreshToken");
		}

		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority((String)claims.get("auth")));

		MemberDTO member = memberService.getMemberById(Long.valueOf(claims.getSubject()));
		if (member == null) {
			request.setAttribute("cannotFindMemberIDInJWTToken", 400);
			throw new JwtAuthenticationException("cannotUseRefreshToken");
		}
		CustomUserDetails userDetails = new CustomUserDetails(member.toEntity());

		return new UsernamePasswordAuthenticationToken(
			userDetails,                          
			accessToken,                         
			userDetails.getAuthorities()         
		);
	}

	public boolean validateToken(HttpServletRequest request, HttpServletResponse response, String token) throws
		JwtAuthenticationException {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			request.setAttribute("invalidJwtToken", 401);
			throw new JwtAuthenticationException("cannotUseRefreshToken");
		} catch (ExpiredJwtException e) {
			request.setAttribute("expiredJwtToken", 401);
			throw new JwtAuthenticationException("cannotUseRefreshToken");
		} catch (UnsupportedJwtException e) {
			request.setAttribute("unsupportedJwtToken", 401);
			throw new JwtAuthenticationException("cannotUseRefreshToken");
		} catch (Exception e) {
			throw new JwtAuthenticationException("cannotUseRefreshToken");
		}
	}

	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(accessToken)
				.getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

}
