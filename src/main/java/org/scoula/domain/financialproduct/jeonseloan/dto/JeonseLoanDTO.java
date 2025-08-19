package org.scoula.domain.financialproduct.jeonseloan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;


@Getter
@Setter
@AllArgsConstructor
@Builder
@ApiModel(description = "전세대출 상품 정보")
public class JeonseLoanDTO {

	@ApiModelProperty(value = "전세대출 상품 ID", example = "1", position = 1)
	private Long jeonseLoanId;

	@ApiModelProperty(value = "금융회사 ID", example = "100", position = 2)
	private Long financialCompanyId;
	@ApiModelProperty(value = "상품명", example = "KB국민은행 전세자금대출", position = 3)
	private String productName;

	@ApiModelProperty(value = "최소 금리", example = "3.5", position = 4)
	private BigDecimal minRate;
	@ApiModelProperty(value = "최대 금리", example = "5.2", position = 5)
	private BigDecimal maxRate;
	@ApiModelProperty(value = "평균 금리", example = "4.35", position = 6)
	private BigDecimal avgRate;

	@ApiModelProperty(value = "최소 대출 금액", example = "50000000", position = 7)
	private Long minLoanAmount;
	@ApiModelProperty(value = "최대 대출 금액", example = "500000000", position = 8)
	private Long maxLoanAmount;
	@ApiModelProperty(value = "최소 대출 기간(개월)", example = "12", position = 9)
	private Integer minPeriod;
	@ApiModelProperty(value = "최대 대출 기간(개월)", example = "360", position = 10)
	private Integer maxPeriod;

	@ApiModelProperty(value = "가입 가능 여부", example = "true", position = 11)
	private Boolean joinAvailable;
	@ApiModelProperty(value = "대출 조건", example = "소득 증빙서류 필수, 신용등급 6등급 이상", position = 12)
	private String loanConditions;
	@ApiModelProperty(value = "금리 정보", example = "기준금리 + 가산금리", position = 13)
	private String rateInfo;


	public static JeonseLoanDTO from(JeonseLoan entity,Boolean joinAvailable) {
		return JeonseLoanDTO.builder()
			.jeonseLoanId(entity.getJeonseLoanId())
			.financialCompanyId(entity.getFinancialCompanyId())
			.productName(entity.getProductName())
			.minRate(entity.getMinRate())
			.maxRate(entity.getMaxRate())
			.avgRate(entity.getAvgRate())
			.minLoanAmount(entity.getMinLoanAmount())
			.maxLoanAmount(entity.getMaxLoanAmount())
			.minPeriod(entity.getMinPeriodMonths())
			.maxPeriod(entity.getMaxPeriodMonths())
			.joinAvailable(joinAvailable)
			.loanConditions(entity.getLoanConditions())
			.rateInfo(entity.getRateInfo())
			.build();
	}
}
