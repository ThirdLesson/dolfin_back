package org.scoula.domain.financialproduct.jeonseloan.controller;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.constants.JeonseLoanInterestFilterType;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.service.JeonseLoanService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jeonse-loan")
@RequiredArgsConstructor
@Slf4j
public class JeonseLoanController {

	private final JeonseLoanService jeonseLoanService;

	//     전세자금대출 전체 조회
	@GetMapping("/jeonse-loan/all")
	@ApiOperation(value = "전세대출상품 리스트 금리 - 최저금리 낮은순 ")
	public SuccessResponse<List<JeonseLoanResponseDTO>> getAllJeonseLoans(
		@RequestParam(required = false, defaultValue = "MIN_RATE") JeonseLoanInterestFilterType filterType){
		List<JeonseLoanResponseDTO> jeonseLoans = jeonseLoanService.getAllJeonseLoans(filterType);
		return SuccessResponse.ok(jeonseLoans);
	}
	//
	// @GetMapping("/jeonse-loan/recommend")
	// @ApiOperation(value = "전세대출상품 리스트 필터링 조회")
	// public SuccessResponse<List<JeonseLoanResponseDTO>> getJeonseLoans(
	// 	@RequestParam(required = false) List<JeonseLoanInterestFilterType> jeonseLoanInterestFilterTypes {
	// 		List<JeonseLoanResponseDTO> jeonseLoanResponseDTOS = jeonseLoanService.get
	// }
	// )

	//    전세자금대출 단건 조회
	// @GetMapping("/{jeonseLoanId}")
	// public SuccessResponse<JeonseLoanDTO> getJeonseLoanById(@PathVariable Long jeonseLoanId) {
	// 	JeonseLoanDTO jeonseLoan = jeonseLoanService.getJeonseLoanById(jeonseLoanId);
	// 	return SuccessResponse.ok(jeonseLoan);
	// }
	//

}
