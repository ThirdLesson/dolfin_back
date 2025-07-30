package org.scoula.domain.financialproduct.financialcompany.dto.response;

import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 금융회사 조회 응답 DTO
// 예금/적금 상세 조회 시, DTO 안에 포함되어 사용자에게 제공됨
@Getter
@AllArgsConstructor
@Builder
public class FinancialCompanyResponseDTO extends BaseEntity {
	private Long id; //금융회사 ID(PK)
	private String name; //금융회사이름
	private String homepageUrl; // 금융회사 홈페이지 주소
	private String callNumber; // 금융회사 대표 전화번호

	// Entity -> DTO
	public static FinancialCompanyResponseDTO fromEntity(FinancialCompany company) {
		return FinancialCompanyResponseDTO.builder()
			.id(company.getFinancialCompanyId())
			.name(company.getName())
			.homepageUrl(company.getHomepageUrl())
			.callNumber(company.getCallNumber())
			.build();
	}
}
