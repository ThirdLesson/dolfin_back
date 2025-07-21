package org.scoula.global.kafka.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogMessage {
	private String logLevel;
	private String timestamp;
	private String txId;
	private String message;
	private Common data;
	private String errorMessage;
}
