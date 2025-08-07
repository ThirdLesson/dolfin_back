package org.scoula.domain.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.scoula.global.exception.CustomException;
import org.scoula.global.exception.errorCode.CommonErrorCode;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Api(tags = "헬스 체크 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Log4j2
public class HealthCheckController {

	@GetMapping
	@ApiOperation("서버 상태 확인")
	public Map<String, Object> healthCheck() {
		log.info("Health check called");

		Map<String, Object> response = new HashMap<>();
		response.put("status", "동작 중");
		response.put("timestamp", System.currentTimeMillis());
		response.put("message", "Server is running");

		return response;
	}

	@GetMapping("/swagger")
	@ApiOperation("Swagger 연동 테스트")
	public Map<String, String> swaggerTest() {

		Map<String, String> response = new HashMap<>();
		response.put("swagger", "enabled");
		response.put("version", "2.0");

		return response;
	}

	@GetMapping("/error")
	@ApiOperation("에러 로그 전송 테스트")
	public SuccessResponse<String> error(HttpServletRequest request) {
		throw new CustomException(CommonErrorCode.INTERNAL_SERVER_ERROR, LogLevel.ERROR, null,
			Common.builder()
				.srcIp(request.getRemoteAddr())
				.apiMethod(request.getMethod())
				.callApiPath(request.getRequestURI())
				.deviceInfo(request.getHeader("user-agent"))
				.build());
	}

}
