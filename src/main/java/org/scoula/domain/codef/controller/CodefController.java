package org.scoula.domain.codef.controller;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.codef.dto.request.StayExpirationRequest;
import org.scoula.domain.codef.dto.response.StayExpirationResponse;
import org.scoula.domain.codef.service.CodefApiClient;
import org.scoula.domain.codef.service.CodefAuthService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "Codef API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/codef")
public class CodefController {

	private final CodefAuthService codefAuthService;
	private final CodefApiClient codefApiClient;

	@ApiOperation(value = "Codef 토큰 발급 API", notes = "Codef 토큰을 발급한 후 레디스에 저장합니다.")
	@PostMapping("/token")
	public SuccessResponse<Void> getCodefToken(HttpServletRequest request) {
		codefAuthService.issueCodefToken(request);
		return SuccessResponse.noContent();
	}

	@ApiOperation(value = "Codef 체류 기간 조회 API", notes = "체류 기간을 조회합니다.")
	@PostMapping("/stay")
	public SuccessResponse<StayExpirationResponse> getCodef(
		@RequestBody StayExpirationRequest stayExpirationRequest, HttpServletRequest request) throws
		JsonProcessingException {
		StayExpirationResponse stayExpiration = codefApiClient.getStayExpiration(stayExpirationRequest, request);
		return SuccessResponse.ok(stayExpiration);
	}
}
