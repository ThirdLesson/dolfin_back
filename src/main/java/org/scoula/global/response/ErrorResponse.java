package org.scoula.global.response;

import lombok.Getter;

@Getter
public class ErrorResponse<T> {
	private final T data;
	private final String code;
	private final String message;

	private ErrorResponse(T data, String code, String message) {
		this.data = data;
		this.code = code;
		this.message = message;
	}

	public static <T> ErrorResponse<T> error(String code, String message) {
		return error(null, code, message);
	}

	public static <T> ErrorResponse<T> error(T data, String code, String message) {
		return new ErrorResponse<>(data, code, message);
	}

}
