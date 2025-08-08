package org.scoula.domain.financialproduct.personalcreditloan.dto.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.scoula.domain.financialproduct.constants.JeonseLoanInterestFilterType;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalLoanSearchRequest {
    
    @NotNull(message = "금리 기준은 필수입니다")
    @ApiModelProperty(value = "금리기준 MIN_RATE|MAX_RATE|AVG_RATE", example = "MIN_RATE", required = true)
    @Pattern(regexp = "MIN_RATE|MAX_RATE|AVG_RATE ", message = "금리 기준은 MIN_RATE|MAX_RATE|AVG_RATE 중 하나여야 합니다")
    private String filterType;  // 대출 금리 기준

    @ApiModelProperty(value = "대출 한도", example = "1000000", required = true)
    @Min(value = 0, message = "최소 대출 한도는 0 이상이어야 합니다")
    private Integer minAmount;    // 최소 대출 한도
    
    private Integer maxAmount;    // 최대 대출 한도

    // 추가 필터링 옵션
    private Integer minPeriod;    // 최소 대출기간(개월)

    private Integer maxPeriod;    // 최대 대출기간(개월)

}
