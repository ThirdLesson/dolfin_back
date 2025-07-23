package org.scoula.domain.financialproduct.jeonseloan.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public SuccessResponse<Void> createJeonseLoan(@RequestBody JeonseLoanDTO jeonseLoanDTO) {
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

    //    금리 범위 검색
    @GetMapping("/rate")
    public SuccessResponse<List<JeonseLoanDTO>> getByInterestRate(
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max) {
        List<JeonseLoanDTO> jeonseLoans = jeonseLoanService.getJeonseLoansByInterestRateRange(min, max);
        return SuccessResponse.ok(jeonseLoans);
    }

    //   외부 API 데이터 가져오기
    @PostMapping("/sync")
    public SuccessResponse<Void> syncFromApi() {
        jeonseLoanService.fetchAndSaveJeonseLoansFromApi();
        return SuccessResponse.created(null);
    }

    //    특정 금융회사 API 데이터 가져오기
    @PostMapping("/sync/company/{financialCompanyId}")
    public SuccessResponse<Void> syncFromApiByCompany(
            @PathVariable Long financialCompanyId) {
        jeonseLoanService.fetchAndSaveJeonseLoansFromApi(financialCompanyId);
        return SuccessResponse.created(null);
    }

    //    API 데이터 동기화 (업데이트/추가)
    @PutMapping("/sync")
    public SuccessResponse<Void> synchronizeFromApi() {
        jeonseLoanService.synchronizeJeonseLoansFromApi();
        return SuccessResponse.noContent();
    }
}
