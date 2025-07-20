package org.scoula.global.kafka.util;

import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.kafka.dto.LogMessage;
import org.springframework.stereotype.Component;

@Component
public class LogMessageMapper {

	public static LogMessage buildLogMessage(
		/* 로그 레벨 ERROR, WARNING, INFO 존재하며,
		ERROR는 아주 심각한 에러로 바로 슬랙에 알림이 가는 심각한 에러,
		WARNING은 비정상적인 시도 접근 등을 의미 1초간 10회 이상 로그가 찍힐 경우 바로 알림 발생,
		INFO는 정보 전달을 위한 로그
		 */
		LogLevel logLevel,
		// 고유 트랜잭션 아이디를 의미 한 번에
		// api call 내부에서 고유한 트랜잭션 아이디를 보유
		// aop를 통해 api가 호출될 때 로그를 발생하고 tx id를 발급할 예정
		// 이후 발급된 tx id를 이용해 계속 로그를 작성하면 됨
		String txId,
		String message,
		// api 경로 의미, 마찬가지로 컨트롤러에서 받아서 로그 작성하면 됨
		String apiPath,
		// GET, POST, PATCH, DELETE 등등,,
		String methodType,
		// 사용자 src ip 의미 헤더에서 추출 가능
		String srcIp,
		// 사용자 디바이스 정보 헤더의 host-Agent를 통해 호출 가능
		String deviceInfo,
		// 거래가 이뤄질 경우 해당 멤버 아이디 기록
		String memberId,
		// 거래가 이뤄질 경우 해당 거래의 회계 코드 기록
		String ledgerCode,
		// 거래의 고유 tx id 기록
		String transactionGroupId,
		// 에러가 발생했을 경우 에러 메세지 기록
		String errorMessage
	) {
		Common common = Common.builder()
			.callApiPath(apiPath)
			.apiMethod(methodType)
			.srcIp(srcIp)
			.deviceInfo(deviceInfo)
			.memberId(memberId)
			.ledgerCode(ledgerCode)
			.transactionGroupId(transactionGroupId)
			.retryCount(0)
			.build();

		return LogMessage.builder()
			.logLevel(logLevel.name())
			.timestamp(String.valueOf(System.currentTimeMillis()))
			.txId(txId)
			.message(message)
			.data(common)
			.errorMessage(errorMessage)
			.build();
	}

	public static LogMessage buildLogMessage(
		LogLevel logLevel,
		String txId,
		String message,
		Common common,
		String errorMessage
	) {
		common.setRetryCount(0);
		return LogMessage.builder()
			.logLevel(logLevel.name())
			.timestamp(String.valueOf(System.currentTimeMillis()))
			.txId(txId)
			.message(message)
			.data(common)
			.errorMessage(errorMessage)
			.build();
	}
}

