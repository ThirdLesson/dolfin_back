package org.scoula.domain.member.exception;

import org.scoula.global.exception.CustomException;
import org.scoula.global.exception.errorCode.ErrorCode;

public class MemberNotFoundException extends CustomException {
	protected MemberNotFoundException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public MemberNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}

	public MemberNotFoundException(ErrorCode errorCode, Object... args) {
		super(errorCode, args);
	}
}
