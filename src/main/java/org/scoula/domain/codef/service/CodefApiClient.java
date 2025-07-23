package org.scoula.domain.codef.service;

import static org.scoula.domain.codef.exception.CodefErrorCode.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.scoula.domain.codef.dto.common.CodefCommonResponse;
import org.scoula.domain.codef.dto.request.StayExpirationRequest;
import org.scoula.domain.codef.dto.response.StayExpirationResponse;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.redis.util.RedisUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodefApiClient {

	private static final String REDIS_ACCESS_TOKEN_KEY = "codef_access_token";
	private final static String STAY_EXPIRATION_URL = "https://development.codef.io/v1/kr/public/mj/hi-korea/stay-expiration-date";

	private final RedisUtil redisUtil;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	public StayExpirationResponse getStayExpiration(StayExpirationRequest stayExpirationRequest) throws
		JsonProcessingException {
		String cachedAccessToken = redisUtil.get(REDIS_ACCESS_TOKEN_KEY).toString();

		// TODO jwt 만료 로직 추가
		if (cachedAccessToken == null) {
			throw new CustomException(CODEF_TOKEN_NOT_FOUND, LogLevel.INFO, null, null);
		}

		HttpEntity<StayExpirationRequest> requestEntity = getRequestHttpEntity(stayExpirationRequest,
			cachedAccessToken);

		CodefCommonResponse<StayExpirationResponse> response = getCodefCommonResponse(requestEntity);

		String resultCode = response.result().code();

		if (resultCode.equals("CF-00000")) {
			return response.data();
		} else if (resultCode.equals("CF-00001")) {
			throw new CustomException(CODEF_REQUIRED_PARAMETER_MISSING, LogLevel.WARNING, null, null);
		} else {
			throw new CustomException(CODEF_STAY_EXPIRATION_API_FAILED, LogLevel.ERROR, null, null);
		}
	}

	private HttpEntity<StayExpirationRequest> getRequestHttpEntity(
		StayExpirationRequest stayExpirationRequest, String cachedAccessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(cachedAccessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		return new HttpEntity<>(stayExpirationRequest, headers);
	}

	private CodefCommonResponse<StayExpirationResponse> getCodefCommonResponse(
		HttpEntity<StayExpirationRequest> requestEntity) throws JsonProcessingException {
		String rawResponse = restTemplate.postForObject(STAY_EXPIRATION_URL, requestEntity, String.class);

		if (rawResponse == null) {
			throw new CustomException(CODEF_STAY_EXPIRATION_API_FAILED, LogLevel.ERROR, null, null);
		}
		String decoded = URLDecoder.decode(rawResponse, StandardCharsets.UTF_8);

		return objectMapper.readValue(decoded, new TypeReference<>() {
		});
	}
}
