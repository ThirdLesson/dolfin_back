package org.scoula.domain.codef.dto.common;

public record CodefResult(
	String code,
	String message,
	String extraMessage, // 추가 메시지 필드 추가
	String transactionId // 트랜잭션 아이디 필드 추가
) {

}
