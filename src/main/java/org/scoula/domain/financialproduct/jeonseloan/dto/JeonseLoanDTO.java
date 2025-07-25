package org.scoula.domain.financialproduct.jeonseloan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class JeonseLoanDTO {
	private final Long jeonseLoanId;
	private final Long financialCompanyId;
	private final String name;
	private final String joinWay;
	private final String loanExpensive;
	private final String erlyFee;
	private final BigDecimal dlyRate;
	private final String loanLmt;
	private final Long jeonseId;
	private final String repayTypeName;
	private final String lendRateTypeName;
	private final BigDecimal lendRateMin;
	private final BigDecimal lendRateMax;
	private final BigDecimal lendRateAvg;

	// DTO → Entity
	public JeonseLoan toEntity() {
		return JeonseLoan.builder()
			.jeonseLoanId(this.jeonseLoanId)
			.financialCompanyId(this.financialCompanyId)
			.name(this.name)
			.joinWay(this.joinWay)
			.loanExpensive(this.loanExpensive)
			.erlyFee(this.erlyFee)
			.dlyRate(this.dlyRate)
			.loanLmt(this.loanLmt)
			.jeonseId(this.jeonseId)
			.repayTypeName(this.repayTypeName)
			.lendRateTypeName(this.lendRateTypeName)
			.lendRateMin(this.lendRateMin)
			.lendRateMax(this.lendRateMax)
			.lendRateAvg(this.lendRateAvg)
			.build();
	}

	// Entity → DTO
	public static JeonseLoanDTO fromEntity(JeonseLoan entity) {
		return JeonseLoanDTO.builder()
			.jeonseLoanId(entity.getJeonseLoanId())
			.financialCompanyId(entity.getFinancialCompanyId())
			.name(entity.getName())
			.joinWay(entity.getJoinWay())
			.loanExpensive(entity.getLoanExpensive())
			.erlyFee(entity.getErlyFee())
			.dlyRate(entity.getDlyRate())
			.loanLmt(entity.getLoanLmt())
			.jeonseId(entity.getJeonseId())
			.repayTypeName(entity.getRepayTypeName())
			.lendRateTypeName(entity.getLendRateTypeName())
			.lendRateMin(entity.getLendRateMin())
			.lendRateMax(entity.getLendRateMax())
			.lendRateAvg(entity.getLendRateAvg())
			.build();
	}
}
