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


@Getter
@AllArgsConstructor
@Builder
@Setter
@ApiModel(description = "금융회사 응답 DTO")
@JsonPropertyOrder({ "id", "name", "homepageUrl", "callNumber" })
public class FinancialCompanyResponseDTO {

	@ApiModelProperty(value = "금융회사 ID", example = "1")
	private Long id; 
	@ApiModelProperty(value = "금융회사 이름", example = "KB국민")
	private String name;
	@ApiModelProperty(value = "금융회사 홈페이지 URL", example = "http://www.kbstar.com")
	private String homepageUrl; 
	@ApiModelProperty(value = "금융회사 대표 전화번호", example = "1588-9999")
	private String callNumber; 


	public static FinancialCompanyResponseDTO fromEntity(FinancialCompany company) {
		return FinancialCompanyResponseDTO.builder()
			.id(company.getFinancialCompanyId())
			.name(company.getName())
			.homepageUrl(company.getHomepageUrl())
			.callNumber(company.getCallNumber())
			.build();
	}
}
