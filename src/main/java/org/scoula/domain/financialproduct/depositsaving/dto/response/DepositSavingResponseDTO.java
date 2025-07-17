package org.scoula.domain.financialproduct.depositsaving.dto.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSaving;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSavingType;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "예금/적금 상품 조회 응답 DTO")
public class DepositSavingResponseDTO {
    @ApiModelProperty(value = "예금/적금 ID", example = "1")
    private Long depositSavingId;

    @ApiModelProperty(value = "금융회사 ID", example = "1")
    private Long financialCompanyId;

    @ApiModelProperty(value = "상품 유형 (예금/적금)", example = "SAVING")
    private DepositSavingType type;

    @ApiModelProperty(value = "상품명", example = "정기적금 1호")
    private String name;

    @ApiModelProperty(value = "가입 방법", example = "온라인/오프라인")
    private String joinWay;

    @ApiModelProperty(value = "이자율 설명")
    private String interestDescription;

    @ApiModelProperty(value = "우대 조건")
    private String spdCondition;

    @ApiModelProperty(value = "기타 유의사항")
    private String etcNote;

    @ApiModelProperty(value = "최고 한도", example = "50000000")
    private String maxLimit;

    @ApiModelProperty(value = "저축 기간(개월)", example = "12")
    private Integer saveMonth;

    @ApiModelProperty(value = "기본 이자율", example = "2.5")
    private BigDecimal interestRate;

    @ApiModelProperty(value = "최고 이자율", example = "3.0")
    private BigDecimal maxInterestRate;

    @ApiModelProperty(value = "이자율 유형", example = "고정/변동")
    private String interestRateType;

    @ApiModelProperty(value = "적립 유형", example = "자유적립/정액적립")
    private String reserveType;

    //    Entity -> DTO
    public static DepositSavingResponseDTO fromEntity(DepositSaving depositSaving) {
        return DepositSavingResponseDTO.builder()
                .depositSavingId(depositSaving.getDepositSavingId())
                .financialCompanyId(depositSaving.getFinancialCompanyId())
                .type(depositSaving.getType())
                .name(depositSaving.getName())
                .joinWay(depositSaving.getJoinWay())
                .interestDescription(depositSaving.getInterestDescription())
                .spdCondition(depositSaving.getSpdCondition())
                .etcNote(depositSaving.getEtcNote())
                .maxLimit(depositSaving.getMaxLimit())
                .saveMonth(depositSaving.getSaveMonth())
                .interestRate(depositSaving.getInterestRate())
                .maxInterestRate(depositSaving.getMaxInterestRate())
                .interestRateType(depositSaving.getInterestRateType())
                .reserveType(depositSaving.getReserveType())
                .build();
    }
}
