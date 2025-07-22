package org.scoula.global.security.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.scoula.domain.member.dto.MemberDTO;
import org.scoula.domain.member.service.MemberService;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.security.exception.JwtErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsernamePwdAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private MemberService memberService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = authentication.getName();
		String pwd = authentication.getCredentials().toString();
		Optional<MemberDTO> customer = Optional.ofNullable(memberService.getMemberByLoginId(username));

		if (customer.isPresent()) {
			if (passwordEncoder.matches(pwd, customer.get().getPassword())) {
				List<GrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority("ROLE_" + "MEMBER"));
				return new UsernamePasswordAuthenticationToken(username, pwd, authorities);

			} else {
				throw new CustomException(JwtErrorMessage.SIGN_IN_FAIL, LogLevel.WARNING, null,
					Common.builder()
						.callApiPath("/auth/signin")
						.apiMethod("POST")
						.memberId(String.valueOf(customer.get().getMemberId()))
						.retryCount(0)
						.build()
				);
			}
		} else {
			throw new CustomException(JwtErrorMessage.SIGN_IN_FAIL, LogLevel.WARNING, null,
				Common.builder()
					.callApiPath("/auth/signin")
					.apiMethod("POST")
					.memberId(String.valueOf(customer.get().getMemberId()))
					.retryCount(0)
					.build()
			);
		}

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
