package org.scoula.global.kafka.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Common {
	private String callApiPath;
	private String apiMethod;
	private String srcIp;
	private String deviceInfo;
	private String memberId;
	private String ledgerCode;
	private String transactionGroupId;
	private Integer retryCount;
}
