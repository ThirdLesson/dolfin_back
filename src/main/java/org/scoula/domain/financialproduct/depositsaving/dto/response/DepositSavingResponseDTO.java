package org.scoula.domain.financialproduct.depositsaving.dto.response;

import org.scoula.domain.financialproduct.depositsaving.dto.DepositSavingDTO;
import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DepositSavingResponseDTO {
	private final DepositSavingDTO product; // 상품정보
	private final FinancialCompanyResponseDTO company; // 금융회사 정보
}
