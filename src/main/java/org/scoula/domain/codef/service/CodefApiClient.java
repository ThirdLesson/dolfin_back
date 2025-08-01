package org.scoula.domain.codef.service;

import static org.scoula.domain.codef.exception.CodefErrorCode.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.account.entity.BankType;
import org.scoula.domain.codef.dto.common.CodefCommonResponse;
import org.scoula.domain.codef.dto.request.StayExpirationRequest;
import org.scoula.domain.codef.dto.response.StayExpirationResponse;
import org.scoula.domain.wallet.dto.request.AccountDepositorRequest;
import org.scoula.domain.wallet.dto.response.DepositorResponse;
import org.scoula.global.exception.CustomException;
import org.scoula.global.exception.errorCode.ErrorCode;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.redis.util.RedisUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodefApiClient {

	private static final String REDIS_ACCESS_TOKEN_KEY = "codef_access_token";
	private final static String STAY_EXPIRATION_URL = "https://development.codef.io/v1/kr/public/mj/hi-korea/stay-expiration-date";
	private static final String ACCOUNT_URL = "https://development.codef.io/v1/kr/bank/a/account/holder";

	private final RedisUtil redisUtil;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final CodefAuthService codefAuthService;

	public StayExpirationResponse getStayExpiration(StayExpirationRequest stayExpirationRequest,
		HttpServletRequest request) throws
		JsonProcessingException {
		String cachedAccessToken = getCachedAccessToken(request);
		Common common = Common.builder()
			.srcIp(request.getRemoteAddr())
			.apiMethod(request.getMethod())
			.callApiPath(request.getRequestURI())
			.deviceInfo(request.getHeader("user-agent"))
			.build();

		HttpEntity<StayExpirationRequest> requestEntity = getRequestHttpEntity(stayExpirationRequest,
			cachedAccessToken);

		CodefCommonResponse<StayExpirationResponse> response = getCodefCommonResponse(requestEntity, common,
			STAY_EXPIRATION_URL, StayExpirationResponse.class, CODEF_STAY_EXPIRATION_API_FAILED);

		String resultCode = response.result().code();

		if (resultCode.equals("CF-00000")) {
			if (response.data().resAuthenticity().equals("0")) {
				// throw new CustomException(STAY_AUTHENTICITY_FAILED, LogLevel.WARNING, null, common);
				return new StayExpirationResponse(
					"0",                                      // resAuthenticity
					"현재 체류중인 외국인이 아닙니다.",              // resAuthenticityDesc
					null,                                     // resPassportNo
					null,                                     // resNationality
					null,                                     // commBirthDate
					null,                                     // resStatus
					LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) // resExpirationDate
				);
			}
			return response.data();
		} else if (resultCode.equals("CF-00001")) {
			throw new CustomException(CODEF_REQUIRED_PARAMETER_MISSING, LogLevel.WARNING, null, common);
		} else {
			throw new CustomException(CODEF_STAY_EXPIRATION_API_FAILED, LogLevel.ERROR, null, common);
		}
	}

	public DepositorResponse verifyAccountHolder(BankType bankType, String accountNumber,
		HttpServletRequest request) throws JsonProcessingException {

		Common common = Common.builder()
			.srcIp(request.getRemoteAddr())
			.apiMethod(request.getMethod())
			.callApiPath(request.getRequestURI())
			.deviceInfo(request.getHeader("user-agent"))
			.build();

		String cachedAccessToken = getCachedAccessToken(request);
		AccountDepositorRequest accountDepositorRequest = new AccountDepositorRequest(bankType.getCode(),
			accountNumber);

		HttpEntity<AccountDepositorRequest> entity = getRequestHttpEntity(accountDepositorRequest,
			cachedAccessToken);

		CodefCommonResponse<DepositorResponse> response = getCodefCommonResponse(entity, null,
			ACCOUNT_URL, DepositorResponse.class, CODEF_ACCOUNT_HOLDER_API_FAILED);

		String resultCode = response.result().code();

		if (resultCode.equals("CF-00000")) {
			return response.data();
		} else if (resultCode.equals("CF-00001")) {
			throw new CustomException(CODEF_REQUIRED_PARAMETER_MISSING, LogLevel.WARNING, null, common,
				response.result().extraMessage() + "가 누락되었습니다.");
		} else {
			throw new CustomException(CODEF_ACCOUNT_HOLDER_API_FAILED, LogLevel.ERROR, null, common);
		}
	}

	private String getCachedAccessToken(HttpServletRequest request) {
		String cachedAccessToken = redisUtil.get(REDIS_ACCESS_TOKEN_KEY);

		if (cachedAccessToken == null) {
			codefAuthService.issueCodefToken(request);
		}
		return cachedAccessToken;
	}

	private <T> HttpEntity<T> getRequestHttpEntity(
		T request, String cachedAccessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(cachedAccessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		return new HttpEntity<>(request, headers);
	}

	private <T, C> CodefCommonResponse<C> getCodefCommonResponse(
		HttpEntity<T> requestEntity, Common common, String url, Class<C> clazz, ErrorCode errorCode) throws
		JsonProcessingException {
		String rawResponse = restTemplate.postForObject(url, requestEntity, String.class);

		if (rawResponse == null) {
			throw new CustomException(errorCode, LogLevel.ERROR, null, common);
		}
		String decoded = URLDecoder.decode(rawResponse, StandardCharsets.UTF_8);

		JavaType type = objectMapper.getTypeFactory()
			.constructParametricType(CodefCommonResponse.class, clazz);

		return objectMapper.readValue(decoded, type);
	}
}
