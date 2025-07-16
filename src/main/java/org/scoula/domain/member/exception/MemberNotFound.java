package org.scoula.domain.member.exception;

import org.scoula.global.exception.CustomException;
import org.scoula.global.exception.errorCode.ErrorCode;

public class MemberNotFound extends CustomException {

	protected MemberNotFound(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

	public MemberNotFound(ErrorCode errorCode) {
		super(errorCode);
	}

	public MemberNotFound(ErrorCode errorCode, Object... args) {
		super(errorCode, args);
	}
}
