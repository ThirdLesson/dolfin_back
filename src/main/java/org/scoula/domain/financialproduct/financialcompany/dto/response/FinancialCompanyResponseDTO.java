package org.scoula.domain.financialproduct.financialcompany.dto.response;

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
@ApiModel(value = "금융회사 응답 DTO", description = "금융회사 상세 정보 응답 데이터")
public class FinancialCompanyResponseDTO {
    @ApiModelProperty(value = "금융회사 ID", example = "1")
    private Long financialCompanyId;

    @ApiModelProperty(value = "금융회사 이름", example = "우리은행")
    private String name;

    @ApiModelProperty(value = "금융회사 코드", example = "WOORI123")
    private String code;
    // Entity -> DTO 변환
    public static FinancialCompanyResponseDTO fromEntity(FinancialCompany financialCompany) {
        return FinancialCompanyResponseDTO.builder()
                .financialCompanyId(financialCompany.getFinancialCompanyId())
                .name(financialCompany.getName())
                .code(financialCompany.getCode())
                .build();
    }
}
