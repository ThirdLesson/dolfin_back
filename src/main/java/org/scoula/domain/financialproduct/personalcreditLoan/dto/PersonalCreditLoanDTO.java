package org.scoula.domain.financialproduct.personalcreditloan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import org.scoula.domain.financialproduct.personalcreditloan.entity.PersonalCreditLoan;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class PersonalCreditLoanDTO {
	private final Long personalCreditLoanId;   // 개인신용대출 아이디 (PK)
	private final Long financialCompanyId;     // 금융회사 아이디 (FK)
	private final String name;                 // 금융상품명
	private final String joinWay;              // 가입 방법
	private final String crdtPrdtTypeNm;       // 대출상품유형
	private final String cbName;               // CB회사명
	private final BigDecimal crdtGrad1;        // 900점 초과
	private final BigDecimal crdtGrad4;        // 801~900점
	private final BigDecimal crdtGrad5;        // 701~800점
	private final BigDecimal crdtGrad6;        // 601~700점
	private final BigDecimal crdtGrad10;       // 501~600점
	private final BigDecimal crdtGrad11;       // 401~500점
	private final BigDecimal crdtGrad12;       // 301~400점
	private final BigDecimal crdtGrad13;       // 300점 이하
	private final BigDecimal crdtGradAvg;      // 평균금리

	//    DTO -> Entity 변환
	public PersonalCreditLoan toEntity() {
		return PersonalCreditLoan.builder()
			.personalCreditLoanId(this.personalCreditLoanId)
			.financialCompanyId(this.financialCompanyId)
			.name(this.name)
			.joinWay(this.joinWay)
			.crdtPrdtTypeNm(this.crdtPrdtTypeNm)
			.cbName(this.cbName)
			.crdtGrad1(this.crdtGrad1)
			.crdtGrad4(this.crdtGrad4)
			.crdtGrad5(this.crdtGrad5)
			.crdtGrad6(this.crdtGrad6)
			.crdtGrad10(this.crdtGrad10)
			.crdtGrad11(this.crdtGrad11)
			.crdtGrad12(this.crdtGrad12)
			.crdtGrad13(this.crdtGrad13)
			.crdtGradAvg(this.crdtGradAvg)
			.build();
	}

	//    Entity -> DTO 변환
	public static PersonalCreditLoanDTO fromEntity(PersonalCreditLoan entity) {
		return PersonalCreditLoanDTO.builder()
			.personalCreditLoanId(entity.getPersonalCreditLoanId())
			.financialCompanyId(entity.getFinancialCompanyId())
			.name(entity.getName())
			.joinWay(entity.getJoinWay())
			.crdtPrdtTypeNm(entity.getCrdtPrdtTypeNm())
			.cbName(entity.getCbName())
			.crdtGrad1(entity.getCrdtGrad1())
			.crdtGrad4(entity.getCrdtGrad4())
			.crdtGrad5(entity.getCrdtGrad5())
			.crdtGrad6(entity.getCrdtGrad6())
			.crdtGrad10(entity.getCrdtGrad10())
			.crdtGrad11(entity.getCrdtGrad11())
			.crdtGrad12(entity.getCrdtGrad12())
			.crdtGrad13(entity.getCrdtGrad13())
			.crdtGradAvg(entity.getCrdtGradAvg())
			.build();
	}
}
