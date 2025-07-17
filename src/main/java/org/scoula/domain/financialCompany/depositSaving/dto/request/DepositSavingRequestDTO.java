package org.scoula.domain.financialCompany.depositSaving.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.scoula.domain.financialCompany.depositSaving.entity.DepositSaving;
import org.scoula.domain.financialCompany.depositSaving.entity.DepositSavingType;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "예금/적금 상품 등록 요청 DTO")
public class DepositSavingRequestDTO {

    @ApiModelProperty(value = "금융회사 ID", example = "1", required = true)
    private Long financialCompanyId;

    @ApiModelProperty(value = "상품 유형 (예금/적금)", example = "SAVING", required = true)
    private DepositSavingType type;

    @ApiModelProperty(value = "상품명", example = "정기예금 1호", required = true)
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

    // DTO → Entity
    public DepositSaving toEntity() {
        return DepositSaving.builder()
                .financialCompanyId(this.financialCompanyId)
                .type(this.type)
                .name(this.name)
                .joinWay(this.joinWay)
                .interestDescription(this.interestDescription)
                .spdCondition(this.spdCondition)
                .etcNote(this.etcNote)
                .maxLimit(this.maxLimit)
                .saveMonth(this.saveMonth)
                .interestRate(this.interestRate)
                .maxInterestRate(this.maxInterestRate)
                .interestRateType(this.interestRateType)
                .reserveType(this.reserveType)
                .build();
    }
}
