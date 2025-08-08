package org.scoula.domain.financialproduct.personalcreditloan.dto.response;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 리스트 응답용
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "개인신용대출 상품 리스트 응답 객체")
public class PersonalLoanListResponse {

    @ApiModelProperty(notes = "개인신용대출 상품 리스트", required = true)
    private List<PersonalSearchResponse> loans;

    @ApiModelProperty(notes = "총 상품 수", example = "100", required = true)
    private int totalCount;


    @ApiModelProperty(notes = "총 페이지 수", example = "10", required = true)
    private int totalPages;


    @ApiModelProperty(notes = "필터링된 금리 유형", example = "MIN_RATE", required = false)
    private String filterType;
}
