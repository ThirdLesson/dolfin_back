package org.scoula.global.response;

import lombok.Getter;

@Getter
public class SuccessResponse<T> {
	private final T data;
	private final int status;
	private final String message;

	private SuccessResponse(T data, int status, String message) {
		this.data = data;
		this.status = status;
		this.message = message;
	}

	public static <T> SuccessResponse<T> ok(T data) {
		return new SuccessResponse<>(data, 200, "OK");
	}

	public static <T> SuccessResponse<T> created(T data) {
		return new SuccessResponse<>(data, 201, "CREATED");
	}

	public static SuccessResponse<Void> noContent() {
		return new SuccessResponse<>(null, 204, "NO_CONTENT");
	}
}
