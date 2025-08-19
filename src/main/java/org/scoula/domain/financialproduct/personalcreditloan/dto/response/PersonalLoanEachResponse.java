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
@ApiModel(description = "개인신용대출 상품 상세 정보 응답 객체")
public class PersonalLoanEachResponse {
    
    @ApiModelProperty(notes = "개인 신용 대출 아이디", example = "1", required = true)
    private Long personalCreditLoanId;
    @ApiModelProperty(notes = "금융상품 명", example = "JB Bravo KOREA 대출", required = true)
    private String productName;         
    @ApiModelProperty(notes = "금융 회사명", example = "JB금융지주", required = true)
    private String companyName;         
    @ApiModelProperty(notes = "금융회사 코드", example = "0370000", required = true)
    private String companyCode;        
    
    @ApiModelProperty(notes = "최저 금리", example = "5.25", required = true)
    private BigDecimal minRate;       
    @ApiModelProperty(notes = "최고 금리", example = "7.50", required = true)
    private BigDecimal maxRate;       
    @ApiModelProperty(notes = "평균 금리", example = "6.38", required = true)
    private BigDecimal avgRate;      

    @ApiModelProperty(notes = "최대 대출 한도", example = "50000000", required = true)
    private BigDecimal maxLoanAmount;  

    @ApiModelProperty(notes = "가입 방법", example = "온라인", required = true)
    private String loanConditions;       

    @ApiModelProperty(notes = "대출 금리 범위", example = "5.25 ~ 7.50", required = true)
    private String RateRange;

    @ApiModelProperty(notes = "가입 가능 여부", example = "true", required = true)
    private boolean joinAvailable; 
    @ApiModelProperty(notes = "최소 대출 기간 (개월)", example = "6", required = true)
    private Integer minPeriod; 
    @ApiModelProperty(notes = "최대 대출 기간 (개월)", example = "60", required = true)
    private Integer maxPeriod; 

    @ApiModelProperty(notes = "홈페이지 URL", example = "https://www.jbfin.co.kr/product/loan/bravo-korea", required = false)
    private String homepageUrl;   
    @ApiModelProperty(notes = "금융상품 콜센터 번호", example = "1588-1234", required = false)
    private String callNumber;     
}

