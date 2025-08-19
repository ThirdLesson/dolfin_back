package org.scoula.domain.financialproduct.personalcreditloan.dto.response;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("개인신용대출 상품 검색 응답 객체")
public class PersonalSearchResponse {

    @ApiModelProperty(notes = "개인 신용 대출 아이디", example = "1", required = true)
    private Long personalCreditLoanId;

    @ApiModelProperty(notes = "금융상품 명", example = "JB Bravo KOREA 대출", required = true)
    private String productName;        

    @ApiModelProperty(notes = "금융 회사명", example = "JB금융지주", required = true)
    private String companyName;        
    @ApiModelProperty(notes = "금융회사 코드", example = "0370000", required = true)
    private String companyCode;         
    
    @ApiModelProperty(notes = "요청된 필터에 따른 금리", example = "5.25", required = true)
    private BigDecimal selectedRate;  

}

