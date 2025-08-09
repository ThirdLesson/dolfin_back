package org.scoula.domain.financialproduct.jeonseloan.dto.response;

import java.math.BigDecimal;

import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

import lombok.Builder;

@Builder
public record JeonseLoanResponseDTO(
	Long jeonseLoanId, //전세 대출 아이디
	String productName,                // 금융상품명
	String companyName, // 금융 회사명
	String companyCode, // 금융 회사 코드
	BigDecimal selectedRate   // 평균금리
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
