package org.scoula.domain.remittancegroup.dto.response;

import lombok.Builder;

@Builder
public record RemittanceGroupCheckResponse (
	Boolean groupExists,
	Long remittanceGroupId,
	String groupCurrency,
	Integer memberCount
){

}
