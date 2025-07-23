package org.scoula.domain.financialproduct.depositsaving.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.scoula.domain.financialproduct.depositsaving.dto.DepositSavingDTO;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSaving;

import java.util.List;

public interface DepositSavingService {

    void createDepositSaving(DepositSavingDTO depositSavingDTO); // 예금 적금 상품 등록

    DepositSavingDTO getDepositSavingById(Long depositSavingId); // 예금,적금 상품 단건 조회

    List<DepositSavingDTO> getAllDepositSavings(); // 예금,적금 상품 전체 목록 조회

    void updateDepositSaving(DepositSavingDTO depositSaving); //예금,적금 상품 수정

    void deleteDepositSaving(Long depositSavingId); // 예금/적금 상품 삭제

    List<DepositSaving> getDepositSavingsByType(String type); // 타입별 예금/적금 상품 조회(예금/적금 구분)

    List<DepositSaving> getDepositSavingsByFinancialCompany(Long financialCompanyId); // 금융회사별 예금/적금 상품 조회

    List<DepositSaving> getDepositSavingsByInterestRateRange(double minRate, double maxRate); //금리 범위로 예금/적금 상품 조회

    void fetchAndSaveDepositSaving() throws JsonProcessingException; // 예금상품API호출

    void fetchAndSaveSavingProducts() throws JsonProcessingException;

}
