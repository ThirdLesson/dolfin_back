package org.scoula.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.scoula.global.exception.errorCode.CommonErrorCode;
import org.scoula.global.exception.errorCode.ErrorCode;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.kafka.producer.KafkaProducer;
import org.scoula.global.kafka.util.LogMessageMapper;
import org.scoula.global.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

	private final KafkaProducer kafkaProducer;

	/**
	 * 커스텀 예외
	 */
	@ExceptionHandler(value = CustomException.class)
	public ResponseEntity<ErrorResponse<Void>> handleCustomException(CustomException e) {
		ErrorCode errorCode = e.getErrorCode();

		kafkaProducer.sendToLogTopic(LogMessageMapper.buildLogMessage(
			e.getLogLevel(),
			e.getTxId(),
			null,
			e.getCommon(),
			e.getMessage()
		));

		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), e.getMessage()));
	}

	/**
	 * 데이터 유효성 검사가 실패할 경우
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ErrorResponse<Map<String, String>>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();
		BindingResult bindingResult = e.getBindingResult();

		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}

		ErrorCode errorCode = CommonErrorCode.INVALID_VALUE;

		kafkaProducer.sendToLogTopic(LogMessageMapper.buildLogMessage(
			LogLevel.WARNING,
			null,
			errorCode.getMessage(),
			Common.builder().build(),
			e.getMessage()
		));

		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errors, errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * 필수 요청 파라미터 누락 예외
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse<Void>> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException e) {
		ErrorCode errorCode = CommonErrorCode.MISSING_PARAMETER;

		kafkaProducer.sendToLogTopic(LogMessageMapper.buildLogMessage(
			LogLevel.WARNING,
			null,
			errorCode.getMessage(),
			Common.builder().build(),
			e.getMessage()
		));

		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
		ErrorCode errorCode = CommonErrorCode.NOT_FOUND;

		kafkaProducer.sendToLogTopic(LogMessageMapper.buildLogMessage(
			LogLevel.WARNING,
			null,
			errorCode.getMessage(),
			Common.builder().build(),
			e.getMessage()
		));

		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), errorCode.getMessage()));
	}

	/**
	 * 예상하지 못한 모든 예외 처리
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse<Void>> handleAllExceptions(Exception e) {
		ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;

		kafkaProducer.sendToLogTopic(LogMessageMapper.buildLogMessage(
			LogLevel.ERROR,
			null,
			errorCode.getMessage(),
			Common.builder().build(),
			e.getMessage()
		));

		log.error(e.getMessage(), e);

		return ResponseEntity.status(errorCode.getHttpStatus())
			.body(ErrorResponse.error(errorCode.getCode(), String.valueOf(e)));
	}
}
