package org.scoula.domain.financialproduct.financialcompany.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialCompany {
    private Long financialCompanyId;
    private String name;
    private String code;
}
