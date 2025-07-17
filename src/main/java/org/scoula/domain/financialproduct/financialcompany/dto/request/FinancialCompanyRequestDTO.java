package org.scoula.domain.financialproduct.financialcompany.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "금융회사 요청 DTO", description = "금융회사 등록/수정 요청 데이터")
public class FinancialCompanyRequestDTO {

    @ApiModelProperty(value = "금융회사 이름", example = "신한은행", required = true)
    private String name;

    @ApiModelProperty(value = "금융회사 코드", example = "SH123", required = true)
    private String code;

    // DTO -> Entity 변환
    public FinancialCompany toEntity() {
        return FinancialCompany.builder()
                .name(this.name)
                .code(this.code)
                .build();
    }
}
