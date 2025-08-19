package org.scoula.domain.financialproduct.jeonseloan.dto.response;

import java.math.BigDecimal;

import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

import lombok.Builder;

@Builder
public record JeonseLoanResponseDTO(
	Long jeonseLoanId, 
	String productName,              
	String companyName, 
	String companyCode, 
	BigDecimal selectedRate   
) {
	public static JeonseLoanResponseDTO from(JeonseLoan entity, String companyName,String companyCode, BigDecimal selectedRate) {
		return JeonseLoanResponseDTO.builder()
			.jeonseLoanId(entity.getJeonseLoanId())
			.productName(entity.getProductName())
			.companyName(companyName)
			.companyCode(companyCode)
			.selectedRate(selectedRate)
			.build();
	}
}
