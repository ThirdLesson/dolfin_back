package org.scoula.domain.financialproduct.jeonseloan.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.domain.financialproduct.jeonseloan.dto.JeonseLoanDTO;
import org.scoula.domain.financialproduct.jeonseloan.service.JeonseLoanService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jeonse-loan")
@RequiredArgsConstructor
@Slf4j
public class JeonseLoanController {

	private final JeonseLoanService jeonseLoanService;

	//    전세자금대출 등록
	@PostMapping
	@ApiOperation(value = "전세 상품 저장 ")
	public SuccessResponse<Void> syncFromExternalApi(@RequestBody JeonseLoanDTO jeonseLoanDTO) {
		jeonseLoanService.createJeonseLoan(jeonseLoanDTO);
		return SuccessResponse.created(null);
	}
	//    전세자금대출 배치 등록
	@PostMapping("/batch")
	public SuccessResponse<Void> createJeonseLoans(@RequestBody List<JeonseLoanDTO> jeonseLoanDTOs) {
		jeonseLoanService.createJeonseLoans(jeonseLoanDTOs);
		return SuccessResponse.created(null);
	}

	//    전세자금대출 단건 조회
	@GetMapping("/{jeonseLoanId}")
	public SuccessResponse<JeonseLoanDTO> getJeonseLoanById(@PathVariable Long jeonseLoanId) {
		JeonseLoanDTO jeonseLoan = jeonseLoanService.getJeonseLoanById(jeonseLoanId);
		return SuccessResponse.ok(jeonseLoan);
	}

	//     전세자금대출 전체 조회
	@GetMapping
	public SuccessResponse<List<JeonseLoanDTO>> getAllJeonseLoans() {
		List<JeonseLoanDTO> jeonseLoans = jeonseLoanService.getAllJeonseLoans();
		return SuccessResponse.ok(jeonseLoans);
	}

	//    전세자금대출 수정
	@PutMapping("/{jeonseLoanId}")
	public SuccessResponse<Void> updateJeonseLoan(
		@PathVariable Long jeonseLoanId,
		@RequestBody JeonseLoanDTO jeonseLoanDTO) {
		jeonseLoanService.updateJeonseLoan(jeonseLoanId, jeonseLoanDTO);
		return SuccessResponse.noContent();
	}

	//   외부 API 데이터 가져오기
	@PostMapping("/sync")
	public SuccessResponse<Void> syncFromApi() {
		jeonseLoanService.fetchAndSaveJeonseLoansFromApi();
		return SuccessResponse.created(null);
	}
}
