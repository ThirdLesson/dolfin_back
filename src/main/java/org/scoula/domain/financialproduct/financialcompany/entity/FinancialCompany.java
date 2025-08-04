package org.scoula.domain.financialproduct.financialcompany.entity;

import org.scoula.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class FinancialCompany extends BaseEntity {
	private Long financialCompanyId;
	private String name;
	private String code;
	private String callNumber;
	private String homepageUrl;
}
