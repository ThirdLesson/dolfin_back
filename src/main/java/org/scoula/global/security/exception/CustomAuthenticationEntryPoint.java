package org.scoula.global.security.exception;

import static org.scoula.global.security.exception.JwtErrorMessage.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scoula.global.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		if (request.getAttribute("signOutException") != null) {
			setResponse(response, SIGN_OUT_USER.getHttpStatus(), SIGN_OUT_USER.getCode(), SIGN_OUT_USER.getMessage());
		} else if (request.getAttribute("cannotUseRefreshToken") != null) {
			setResponse(response, CANNOT_USE_REFRESH_TOKEN.getHttpStatus(), CANNOT_USE_REFRESH_TOKEN.getCode(),
				CANNOT_USE_REFRESH_TOKEN.getMessage());
		} else if (request.getAttribute("invalidJwtToken") != null) {
			setResponse(response, INVALID_JWT_TOKEN.getHttpStatus(), INVALID_JWT_TOKEN.getCode(),
				INVALID_JWT_TOKEN.getMessage());
		} else if (request.getAttribute("expiredJwtToken") != null) {
			setResponse(response, NEED_ACCESS_TOKEN_REFRESH.getHttpStatus(), NEED_ACCESS_TOKEN_REFRESH.getCode(),
				NEED_ACCESS_TOKEN_REFRESH.getMessage());
		} else if (request.getAttribute("notExistAccessToken") != null) {
			setResponse(response, NEED_ACCESS_TOKEN_REFRESH.getHttpStatus(), NEED_ACCESS_TOKEN_REFRESH.getCode(),
				NEED_ACCESS_TOKEN_REFRESH.getMessage());
		} else if (request.getAttribute("unsupportedJwtToken") != null) {
			setResponse(response, UNSUPPORTED_JWT_TOKEN.getHttpStatus(), UNSUPPORTED_JWT_TOKEN.getCode(),
				UNSUPPORTED_JWT_TOKEN.getMessage());
		} else if (request.getAttribute("cannotFindMemberIDInJWTToken") != null) {
			setResponse(response, IN_JWT_NOT_CONTAINS_USER_INFO.getHttpStatus(),
				IN_JWT_NOT_CONTAINS_USER_INFO.getCode(),
				IN_JWT_NOT_CONTAINS_USER_INFO.getMessage());
		} else {
			// 나머지 잘못된 요청에 대한 기본 예외 처리
			setResponse(response, HttpStatus.UNAUTHORIZED, "F-001",
				"허용되지 않은 권한 또는 허용되지 않은 경로입니다."); // 기본적으로 401 상태 코드와 에러 메시지 반환
		}

	}

	// 발생한 예외에 맞게 status를 설정하고 message를 반환
	private void setResponse(HttpServletResponse response, HttpStatus status, String code, String message) throws
		IOException {
		response.setStatus(status.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String json = new ObjectMapper().writeValueAsString(ErrorResponse.error(code, message));
		response.getWriter().write(json);
	}

}
