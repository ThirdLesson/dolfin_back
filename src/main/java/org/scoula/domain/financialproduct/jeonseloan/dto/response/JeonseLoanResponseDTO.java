package org.scoula.domain.financialproduct.jeonseloan.dto.response;

import java.math.BigDecimal;

import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.JeonseLoanDTO;

import lombok.Builder;

@Builder
public record JeonseLoanResponseDTO(
	Long id,
	String name,                // 상품명
	String companyName,         // 금융회사명
	BigDecimal lendRateMin,     // 최저 금리
	BigDecimal lendRateMax,      // 최고 금리
	FinancialCompanyResponseDTO company // 회사 정보
) {
}
