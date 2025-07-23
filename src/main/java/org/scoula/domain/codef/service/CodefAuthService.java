package org.scoula.domain.codef.service;

import static org.scoula.domain.codef.exception.CodefErrorCode.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.codef.dto.response.CodefTokenResponse;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodefAuthService {

	private static final String OAUTH_ENDPOINT = "https://oauth.codef.io/oauth/token";
	private static final String REDIS_ACCESS_TOKEN_KEY = "codef_access_token";
	private final RestTemplate restTemplate;
	private final RedisUtil redisUtil;
	@Value("${codef.client.id}")
	private String clientId;
	@Value("${codef.client.secret}")
	private String clientSecret;

	public void issueCodefToken(HttpServletRequest request) {
		HttpEntity<MultiValueMap<String, String>> requestEntity = getRequestEntity();

		ResponseEntity<CodefTokenResponse> responseEntity = restTemplate.postForEntity(
			OAUTH_ENDPOINT, requestEntity, CodefTokenResponse.class);

		CodefTokenResponse response = responseEntity.getBody();
		String codefToken = response.accessToken();
		Integer expiresIn = response.expiresIn();

		Common common = Common.builder()
			.srcIp(request.getRemoteAddr())
			.apiMethod(request.getMethod())
			.callApiPath(request.getRequestURI())
			.deviceInfo(request.getHeader("user-agent"))
			.build();

		if (codefToken == null || expiresIn == null) {
			throw new CustomException(CODEF_TOKEN_NOT_FOUND, LogLevel.INFO, null, common);
		}

		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			redisUtil.set(REDIS_ACCESS_TOKEN_KEY, codefToken, expiresIn);
		} else {
			throw new CustomException(CODEF_TOKEN_API_FAILED, LogLevel.ERROR, null, common);
		}
	}

	private HttpEntity<MultiValueMap<String, String>> getRequestEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String auth = clientId + ":" + clientSecret;
		String authStringEnc = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
		headers.set("Authorization", "Basic " + authStringEnc);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "client_credentials");
		params.add("scope", "read");

		return new HttpEntity<>(params, headers);
	}
}
