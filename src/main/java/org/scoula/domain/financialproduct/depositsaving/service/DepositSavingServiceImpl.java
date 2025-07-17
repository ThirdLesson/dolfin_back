package org.scoula.domain.financialproduct.depositsaving.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSaving;
import org.scoula.domain.financialproduct.depositsaving.mapper.DepositSavingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositSavingServiceImpl implements DepositSavingService {

   private final DepositSavingMapper depositSavingMapper;

    @Transactional
    @Override
    public void createDepositSaving(DepositSaving depositSaving) {
        log.info("createDeposiSaving : {}",depositSaving);
        depositSavingMapper.insertDepositSaving(depositSaving);
    }

    @Override
    public DepositSaving getDepositSavingById(Long depositSavingId) {
        log.info("getDepositSavingById : {}",depositSavingId);
        return depositSavingMapper.selectDepositSavingById(depositSavingId);
    }


    @Override
    public List<DepositSaving> getAllDepositSavings() {
        log.info("getAllDepositSavings");
        return depositSavingMapper.selectAllDepositSavings();
    }

    @Transactional
    @Override
    public void updateDepositSaving(DepositSaving depositSaving) {
        log.info("updateDepositSaving : {}",depositSaving);
        depositSavingMapper.updateDepositSaving(depositSaving);
    }

    @Transactional
    @Override
    public void deleteDepositSaving(Long depositSavingId) {
        log.info("deleteDepositSaving : {}",depositSavingId);
        depositSavingMapper.deleteDepositSaving(depositSavingId);
    }

    @Override
    public List<DepositSaving> getDepositSavingsByType(String type) {
        log.info("getDepositSavingsByType : {}",type);
        return depositSavingMapper.selectAllDepositSavings();
    }

    @Override
    public List<DepositSaving> getDepositSavingsByFinancialCompany(Long financialCompanyId) {
        log.info("getDepositSavingByFinancialCompany : {}",financialCompanyId);
        return depositSavingMapper.selectAllDepositSavings();
    }

    @Override
    public List<DepositSaving> getDepositSavingsByInterestRateRange(double minRate, double maxRate) {
        log.info("getDepositSavingsByInterestRateRange : {},{}",minRate,maxRate);
        return depositSavingMapper.selectAllDepositSavings();
    }
}
