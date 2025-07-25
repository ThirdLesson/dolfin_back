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
	private String cal_tel; // 콜센터전화번호
	private String homp_url; // 홈페이지 주소

	//    dto -> entity
	public FinancialCompany toEntity() {
		return FinancialCompany.builder()
			.code(fin_co_no)
			.name(kor_co_nm)
			.callNumber(cal_tel)
			.hompageUrl(homp_url)
			.build();
	}

	//    entity -> dto
	public static FinancialCompanyDTO fromEntity(FinancialCompany entity) {
		if (entity == null)
			return null;
		return FinancialCompanyDTO.builder()
			.fin_co_no(entity.getCode())
			.kor_co_nm(entity.getName())
			.cal_tel(entity.getCallNumber())
			.homp_url(entity.getHompageUrl())
			.build();
	}
}
