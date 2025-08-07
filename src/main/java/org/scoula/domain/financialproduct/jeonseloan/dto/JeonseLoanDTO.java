package org.scoula.domain.financialproduct.jeonseloan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import org.scoula.domain.financialproduct.constants.JeonseLoanInterestFilterType;
import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;
	private List<JeonseLoanInterestFilterType> jeonseLoanInterestFilterTypes;

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
			.build();
	}

	// Entity → DTO
	public static JeonseLoanDTO fromEntity(JeonseLoan entity, List<JeonseLoanInterestFilterType> jeonseLoanInterestFilterTypes) {
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
			.jeonseLoanInterestFilterTypes(jeonseLoanInterestFilterTypes)
			.createdAt(entity.getCreatedAt())
			.updatedAt(entity.getUpdatedAt())
			.build();
	}
}
