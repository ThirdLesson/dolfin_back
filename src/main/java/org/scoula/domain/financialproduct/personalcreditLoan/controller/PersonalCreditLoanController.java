package org.scoula.domain.financialproduct.personalcreditloan.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.personalcreditloan.dto.PersonalCreditLoanDTO;
import org.scoula.domain.financialproduct.personalcreditloan.service.PersonalCreditLoanService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/personal-credit-loans")
public class PersonalCreditLoanController {

	private final PersonalCreditLoanService personalCreditLoanService;

	@PostMapping
	public SuccessResponse<Void> createPersonalCreditLoan(@RequestBody PersonalCreditLoanDTO personalCreditLoanDTO) {
		log.info("개인신용대출 상품 등록 요청 :{}", personalCreditLoanDTO);
		personalCreditLoanService.createPersonalCreditLoan(personalCreditLoanDTO);
		return SuccessResponse.created(null);
	}

	@GetMapping("/{personalCreditLoanId}")
	public SuccessResponse<PersonalCreditLoanDTO> getPersonalCreditLoanById(
		@PathVariable Long personalCreditLoanId) {
		log.info("개인신용대출 상품 단건 조회 요청 : {}", personalCreditLoanId);

		PersonalCreditLoanDTO personalCreditLoanDTO = personalCreditLoanService.getPersonalCreditLoanById(
			personalCreditLoanId);

		return SuccessResponse.ok(personalCreditLoanDTO);
	}

	@GetMapping
	public SuccessResponse<List<PersonalCreditLoanDTO>> getAllPersonalCreditLoans() {
		log.info("전체 개인신용대출 상품 조회 요청");

		List<PersonalCreditLoanDTO> personalCreditLoans = personalCreditLoanService.getAllPersonalCreditLoans();

		return SuccessResponse.ok(personalCreditLoans);
	}

	@PutMapping("/{personalCreditLoanId}")
	public SuccessResponse<Void> updatePersonalCreditLoan(
		@PathVariable Long personalCreditLoanId,
		@RequestBody PersonalCreditLoanDTO personalCreditLoanDTO) {
		log.info("개인신용대출 상품 수정 요청: {}, {}", personalCreditLoanId, personalCreditLoanDTO);

		personalCreditLoanService.updatePersonalCreditLoan(personalCreditLoanId, personalCreditLoanDTO);

		return SuccessResponse.ok(null);
	}

	@GetMapping("/financial-company/{financialCompanyId}")
	public SuccessResponse<List<PersonalCreditLoanDTO>> getPersonalCreditLoansByFinancialCompany(
		@PathVariable Long financialCompanyId) {
		log.info("금융회사별 개인신용대출 상품 조회 요청: {}", financialCompanyId);

		List<PersonalCreditLoanDTO> personalCreditLoans = personalCreditLoanService.getPersonalCreditLoansByFinancialCompanyId(
			financialCompanyId);

		return SuccessResponse.ok(personalCreditLoans);
	}

	@GetMapping("/product-type/{crdtPrdtTypeNm}")
	public SuccessResponse<List<PersonalCreditLoanDTO>> getPersonalCreditLoansByProductType(
		@PathVariable String crdtPrdtTypeNm) {
		log.info("대출상품유형별 개인신용대출 상품 조회 요청: {}", crdtPrdtTypeNm);

		List<PersonalCreditLoanDTO> personalCreditLoans = personalCreditLoanService.getPersonalCreditLoansByCrdtPrdtTypeNm(
			crdtPrdtTypeNm);

		return SuccessResponse.ok(personalCreditLoans);
	}

	@GetMapping("/sorted-by-rate")
	public SuccessResponse<List<PersonalCreditLoanDTO>> getPersonalCreditLoansSortedByRate() {
		log.info("평균금리순 개인신용대출 상품 조회 요청");

		List<PersonalCreditLoanDTO> personalCreditLoans = personalCreditLoanService.getPersonalCreditLoansOrderByAvgRate();

		return SuccessResponse.ok(personalCreditLoans);
	}

	@PostMapping("/sync")
	public SuccessResponse<Void> synchronizePersonalCreditLoanData() {
		log.info("개인신용대출 데이터 동기화 요청");

		personalCreditLoanService.synchronizePersonalCreditLoanData();

		return SuccessResponse.ok(null);
	}

	@PostMapping("/batch")
	public SuccessResponse<Void> savePersonalCreditLoansBatch(
		@RequestBody List<PersonalCreditLoanDTO> personalCreditLoanDtos) {
		log.info("개인신용대출 배치 저장 요청: {}개", personalCreditLoanDtos.size());

		personalCreditLoanService.savePersonalCreditLoansBatch(personalCreditLoanDtos);

		return SuccessResponse.ok(null);
	}
}
