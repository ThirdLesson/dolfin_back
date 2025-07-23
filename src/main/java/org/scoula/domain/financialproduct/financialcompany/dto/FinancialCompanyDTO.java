package org.scoula.domain.financialproduct.financialcompany.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialCompanyDTO {
    private String fin_co_no; // 금융회사 코드
    private String kor_co_nm; // 금융회사 이름

    //    dto -> entity
    public FinancialCompany toEntity() {
        return FinancialCompany.builder()
                .code(fin_co_no)
                .name(kor_co_nm)
                .build();
    }

    //    entity -> dto
    public static FinancialCompanyDTO fromEntity(FinancialCompany entity) {
        if(entity == null) return null;
        return FinancialCompanyDTO.builder()
                .fin_co_no(entity.getCode())
                .kor_co_nm(entity.getName())
                .build();
    }

}
