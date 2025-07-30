package org.scoula.domain.financialproduct.financialcompany.dto.request;

import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 금융회사 등록 요청 DTO
// 외부 데이터 소스에서 받은 금융회사 정보를 DB에 저장하기 위한 DTO
@Getter
@AllArgsConstructor
@Builder
public class FinancialCompanyRequestDTO extends BaseEntity {
	private final String finCoNo; // 금융회사 코드
	private final String name; // 금융회사 이름
	private final String homepageUrl; // 금융회사 홈페이지 주소
	private final String callNumber; // 금웅회사 전화번호

	// DTO -> ENTITY
	public FinancialCompany toEntity() {
		return FinancialCompany.builder()
			.code(this.finCoNo)
			.name(this.name)
			.homepageUrl(null)
			.callNumber(null)
			.build();
	}
}
