package org.scoula.domain.financialproduct.jeonseloan.dto.response;

import java.math.BigDecimal;

import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.JeonseLoanDTO;
import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

import lombok.Builder;

@Builder
public record JeonseLoanResponseDTO(
	Long id,
	String name,                // 상품명
	BigDecimal lendRateAVG,     // 평균금리
	FinancialCompanyResponseDTO company // 회사 정보
) {
	public static JeonseLoanResponseDTO from(JeonseLoan entity, FinancialCompanyResponseDTO company) {
		return new JeonseLoanResponseDTO(
			entity.getJeonseLoanId(),
			entity.getName(),
			entity.getLendRateAvg(),
			company
		);
	}
}
