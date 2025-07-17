package org.scoula.domain.financialCompany.jeonseLoan.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.scoula.domain.financialCompany.jeonseLoan.entity.JeonseLoan;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "전세자금대출 응답 DTO")
public class JeonseLoanResponseDTO {

    @ApiModelProperty(value = "전세자금대출 ID", example = "1")
    private Long jeonseLoanId;

    @ApiModelProperty(value = "금융회사 ID", example = "1")
    private Long financialCompanyId;

    @ApiModelProperty(value = "금융상품명", example = "청년전세자금대출")
    private String name;

    @ApiModelProperty(value = "가입 방법", example = "인터넷뱅킹")
    private String joinWay;

    @ApiModelProperty(value = "대출 부대비용", example = "없음")
    private String loanExpensive;

    @ApiModelProperty(value = "중도상환 수수료", example = "1.0%")
    private String erlyFee;

    @ApiModelProperty(value = "연체 이자율", example = "5.5")
    private BigDecimal dlyRate;

    @ApiModelProperty(value = "대출 한도", example = "1억원")
    private String loanLmt;

    @ApiModelProperty(value = "전세대출 기본정보 ID", example = "2")
    private Long jeonseId;

    @ApiModelProperty(value = "대출 상환 유형명", example = "원리금균등상환")
    private String repayTypeName;

    @ApiModelProperty(value = "대출 금리 유형명", example = "고정금리")
    private String lendRateTypeName;

    @ApiModelProperty(value = "대출금리 최저", example = "2.5")
    private BigDecimal lendRateMin;

    @ApiModelProperty(value = "대출금리 최고", example = "4.0")
    private BigDecimal lendRateMax;

    @ApiModelProperty(value = "전체 취급 평균금리", example = "3.2")
    private BigDecimal lendRateAvg;

    // Entity -> DTO
    public static JeonseLoanResponseDTO fromEntity(JeonseLoan jeonseLoan) {
        return JeonseLoanResponseDTO.builder()
                .jeonseLoanId(jeonseLoan.getJeonseLoanId())
                .financialCompanyId(jeonseLoan.getFinancialCompanyId())
                .name(jeonseLoan.getName())
                .joinWay(jeonseLoan.getJoinWay())
                .loanExpensive(jeonseLoan.getLoanExpensive())
                .erlyFee(jeonseLoan.getErlyFee())
                .dlyRate(jeonseLoan.getDlyRate())
                .loanLmt(jeonseLoan.getLoanLmt())
                .jeonseId(jeonseLoan.getJeonseId())
                .repayTypeName(jeonseLoan.getRepayTypeName())
                .lendRateTypeName(jeonseLoan.getLendRateTypeName())
                .lendRateMin(jeonseLoan.getLendRateMin())
                .lendRateMax(jeonseLoan.getLendRateMax())
                .lendRateAvg(jeonseLoan.getLendRateAvg())
                .build();
    }
}
