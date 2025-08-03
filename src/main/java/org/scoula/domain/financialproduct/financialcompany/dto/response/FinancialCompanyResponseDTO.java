package org.scoula.domain.financialproduct.financialcompany.dto.response;

import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.global.entity.BaseEntity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// 금융회사 조회 응답 DTO
// 예금/적금 상세 조회 시, DTO 안에 포함되어 사용자에게 제공됨
@Getter
@AllArgsConstructor
@Builder
@Setter
@ApiModel(description = "금융회사 응답 DTO")
@JsonPropertyOrder({ "id", "name", "homepageUrl", "callNumber" })
public class FinancialCompanyResponseDTO {

	@ApiModelProperty(value = "금융회사 ID", example = "1")
	private Long id; //금융회사 ID(PK)
	@ApiModelProperty(value = "금융회사 이름", example = "KB국민")
	private String name; //금융회사이름
	@ApiModelProperty(value = "금융회사 홈페이지 URL", example = "http://www.kbstar.com")
	private String homepageUrl; // 금융회사 홈페이지 주소
	@ApiModelProperty(value = "금융회사 대표 전화번호", example = "1588-9999")
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
