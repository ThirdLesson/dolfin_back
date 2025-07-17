package org.scoula.domain.financialproduct.personalcreditLoan.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import org.scoula.domain.financialproduct.personalcreditLoan.entity.PersonalCreditLoan;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "PersonalCreditLoanResponseDTO", description = "개인신용대출 응답 DTO")
public class PersonalCreditLoanResponseDTO {

    @ApiModelProperty(value = "개인신용대출 아이디", example = "1")
    private Long personalCreditLoanId;

    @ApiModelProperty(value = "금융회사 아이디", example = "1")
    private Long financialCompanyId;

    @ApiModelProperty(value = "금융상품명", example = "스마트 개인 신용대출")
    private String name;

    @ApiModelProperty(value = "가입 방법", example = "온라인")
    private String joinWay;

    @ApiModelProperty(value = "대출상품유형", example = "일반신용대출")
    private String crdtPrdtTypeNm;

    @ApiModelProperty(value = "CB회사명", example = "KCB")
    private String cbName;

    @ApiModelProperty(value = "900점 초과", example = "4.5")
    private BigDecimal crdtGrad1;

    @ApiModelProperty(value = "801~900점", example = "4.8")
    private BigDecimal crdtGrad4;

    @ApiModelProperty(value = "701~800점", example = "5.2")
    private BigDecimal crdtGrad5;

    @ApiModelProperty(value = "601~700점", example = "5.5")
    private BigDecimal crdtGrad6;

    @ApiModelProperty(value = "501~600점", example = "6.0")
    private BigDecimal crdtGrad10;

    @ApiModelProperty(value = "401~500점", example = "7.0")
    private BigDecimal crdtGrad11;

    @ApiModelProperty(value = "301~400점", example = "8.0")
    private BigDecimal crdtGrad12;

    @ApiModelProperty(value = "300점 이하", example = "9.0")
    private BigDecimal crdtGrad13;

    @ApiModelProperty(value = "평균 금리", example = "5.5")
    private BigDecimal crdtGradAvg;

    // Entity -> DTO 변환
    public static PersonalCreditLoanResponseDTO fromEntity(PersonalCreditLoan entity) {
        return PersonalCreditLoanResponseDTO.builder()
                .personalCreditLoanId(entity.getPersonalCreditLoanId())
                .financialCompanyId(entity.getFinancialCompanyId())
                .name(entity.getName())
                .joinWay(entity.getJoinWay())
                .crdtPrdtTypeNm(entity.getCrdtPrdtTypeNm())
                .cbName(entity.getCbName())
                .crdtGrad1(entity.getCrdtGrad1())
                .crdtGrad4(entity.getCrdtGrad4())
                .crdtGrad5(entity.getCrdtGrad5())
                .crdtGrad6(entity.getCrdtGrad6())
                .crdtGrad10(entity.getCrdtGrad10())
                .crdtGrad11(entity.getCrdtGrad11())
                .crdtGrad12(entity.getCrdtGrad12())
                .crdtGrad13(entity.getCrdtGrad13())
                .crdtGradAvg(entity.getCrdtGradAvg())
                .build();
    }
}
