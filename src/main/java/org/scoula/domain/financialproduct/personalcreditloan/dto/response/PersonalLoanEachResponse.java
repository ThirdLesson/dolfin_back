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
    
    // 상품 기본 정보
    @ApiModelProperty(notes = "개인 신용 대출 아이디", example = "1", required = true)
    private Long personalCreditLoanId;
    @ApiModelProperty(notes = "금융상품 명", example = "JB Bravo KOREA 대출", required = true)
    private String productName;         // 금융상품명
    @ApiModelProperty(notes = "금융 회사명", example = "JB금융지주", required = true)
    private String companyName;         // 금융회사명
    @ApiModelProperty(notes = "금융회사 코드", example = "0370000", required = true)
    private String companyCode;         // 금융회사 코드
    
    // 금리 정보
    @ApiModelProperty(notes = "최저 금리", example = "5.25", required = true)
    private BigDecimal minRate;       // 최저금리
    @ApiModelProperty(notes = "최고 금리", example = "7.50", required = true)
    private BigDecimal maxRate;       // 최고금리
    @ApiModelProperty(notes = "평균 금리", example = "6.38", required = true)
    private BigDecimal avgRate;       // 평균금리

    @ApiModelProperty(notes = "최대 대출 한도", example = "50000000", required = true)
    private BigDecimal maxLoanAmount;  // 최대 대출 한도

    @ApiModelProperty(notes = "가입 방법", example = "온라인", required = true)
    private String loanConditions;         // 대출 조건

    @ApiModelProperty(notes = "대출 금리 범위", example = "5.25 ~ 7.50", required = true)
    private String RateRange; //

    @ApiModelProperty(notes = "가입 가능 여부", example = "true", required = true)
    private boolean joinAvailable; // 가입 가능 여부

    // 대출 기간 정보
    @ApiModelProperty(notes = "최소 대출 기간 (개월)", example = "6", required = true)
    private Integer minPeriod; // 최소 대출 기간(개월)
    @ApiModelProperty(notes = "최대 대출 기간 (개월)", example = "60", required = true)
    private Integer maxPeriod; // 최대 대출 기간(개월)

    // 추가 정보
    @ApiModelProperty(notes = "홈페이지 URL", example = "https://www.jbfin.co.kr/product/loan/bravo-korea", required = false)
    private String homepageUrl;    // 금융상품 홈페이지 URL
    @ApiModelProperty(notes = "금융상품 콜센터 번호", example = "1588-1234", required = false)
    private String callNumber;     // 금융상품 콜센터 번호
}

