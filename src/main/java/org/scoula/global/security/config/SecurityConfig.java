package org.scoula.global.security.config;

import java.util.List;

import org.scoula.global.security.exception.CustomAuthenticationEntryPoint;
import org.scoula.global.security.filter.JwtAuthenticationFilter;
import org.scoula.global.security.util.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomAuthenticationEntryPoint customAuthenticationExceptionHandler;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();

		corsConfiguration.setAllowedOriginPatterns(
			List.of("http://localhost:5173", "https://localhost:5173"));

		corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "PATCH"));
		corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
		corsConfiguration.setExposedHeaders(List.of("Set-Cookie"));
		corsConfiguration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);

		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(
			corsConfigurationSource()));

		http.csrf(AbstractHttpConfigurer::disable);

		http.sessionManagement((session) -> session
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
			authorizationManagerRequestMatcherRegistry

				// 로그인 관련 api
				.requestMatchers("/auth/**").permitAll()
				// 스웨거 사용을 위한 허용
				.requestMatchers(
					"/v2/api-docs/**",
					"/swagger-resources/**",
					"/swagger-ui.html/**",
					"/swagger-ui/**",
					"/webjars/**",
					"/configuration/**"
				).permitAll()
				.anyRequest().authenticated()
		);

		http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customAuthenticationExceptionHandler),
			UsernamePasswordAuthenticationFilter.class);

		http.formLogin().disable();

		http.exceptionHandling()
			.authenticationEntryPoint(customAuthenticationEntryPoint);

		return http.build();
	}

}
