package org.scoula.global.kafka.util;

import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.kafka.dto.LogMessage;
import org.springframework.stereotype.Component;

@Component
public class LogMessageMapper {

	public static LogMessage buildLogMessage(
		
		LogLevel logLevel,
		
		String txId,
		String message,
		String apiPath,
		String methodType,
		String srcIp,
		String deviceInfo,
		String memberId,
		String ledgerCode,
		String transactionGroupId,
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

