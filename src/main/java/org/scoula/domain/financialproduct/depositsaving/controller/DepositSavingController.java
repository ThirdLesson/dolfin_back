package org.scoula.domain.financialproduct.depositsaving.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.domain.financialproduct.depositsaving.dto.DepositSavingDTO;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSaving;
import org.scoula.domain.financialproduct.depositsaving.service.DepositSavingService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deposit-savings")
@RequiredArgsConstructor
@Slf4j
public class DepositSavingController {

    private final DepositSavingService depositSavingService;

    // 예금 상품 API 호출 및 저장
    @PostMapping("/sync/deposits")
    public SuccessResponse<String> syncFromExternalApi() throws JsonProcessingException {
        depositSavingService.fetchAndSaveDepositSaving();
        return SuccessResponse.ok("예금 상품 동기화가 완료되었습니다.");
    }

    // 적금 상품 API 호출 및 저장
    @PostMapping("/sync/savings")
    public SuccessResponse<String> syncSavings() throws JsonProcessingException {
        depositSavingService.fetchAndSaveSavingProducts();
        return SuccessResponse.ok("적금 상품 동기화가 완료되었습니다.");
    }

    //  전체 동기화 (예금 + 적금)
    @PostMapping("/sync/all")
    public SuccessResponse<String> syncAll() throws JsonProcessingException {
        depositSavingService.fetchAndSaveDepositSaving();
        depositSavingService.fetchAndSaveSavingProducts();
        return SuccessResponse.ok("모든 상품 동기화가 완료되었습니다.");
    }

    // 전체 조회
    @GetMapping
    public SuccessResponse<List<DepositSavingDTO>> getAll() {
        List<DepositSavingDTO> list = depositSavingService.getAllDepositSavings();
        return SuccessResponse.ok(list);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public SuccessResponse<DepositSavingDTO> getById(@PathVariable Long id) {
        DepositSavingDTO dto = depositSavingService.getDepositSavingById(id);
        return SuccessResponse.ok(dto);
    }

    // 타입별 조회 (예금/적금)
    @GetMapping("/type/{type}")
    public SuccessResponse<List<DepositSaving>> getProductsByType(@PathVariable String type) {
        List<DepositSaving> list = depositSavingService.getDepositSavingsByType(type);
        return SuccessResponse.ok(list);
    }

    // 금융회사별 조회
    @GetMapping("/company/{companyId}")
    public SuccessResponse<List<DepositSaving>> getProductsByCompany(@PathVariable Long companyId) {
        List<DepositSaving> list = depositSavingService.getDepositSavingsByFinancialCompany(companyId);
        return SuccessResponse.ok(list);
    }

    // 금리 범위별 조회
    @GetMapping("/interest-rate")
    public SuccessResponse<List<DepositSaving>> getProductsByInterestRate(
            @RequestParam double minRate,
            @RequestParam double maxRate) {
        List<DepositSaving> list = depositSavingService.getDepositSavingsByInterestRateRange(minRate, maxRate);
        return SuccessResponse.ok(list);
    }
}
