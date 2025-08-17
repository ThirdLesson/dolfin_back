package org.scoula.domain.ledger.service;// LedgerCodeService.java
import static org.scoula.domain.wallet.exception.WalletErrorCode.*;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.ledger.entity.LedgerCode;
import org.scoula.domain.ledger.mapper.LedgerCodeMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LedgerCodeService {

    private final LedgerCodeMapper ledgerCodeMapper;

    @Cacheable(value = "ledgerCodes", key = "#name")
    public LedgerCode findByName(String name, HttpServletRequest servletRequest) {
        // 이 메서드는 "BANK_ASSET", "BANK_PAYABLE" 각각 최초 한 번씩만 호출됩니다.
        return ledgerCodeMapper.findByName(name)
                   .orElseThrow(() -> new CustomException(LEDGER_CODE_NOT_FOUND, LogLevel.ERROR, null,
                       Common.builder()
                           .ledgerCode("BANK_PAYABLE")
                           .srcIp(servletRequest.getRemoteAddr())
                           .callApiPath(servletRequest.getRequestURI())
                           .apiMethod(servletRequest.getMethod())
                           .deviceInfo(servletRequest.getHeader("user-agent"))
                           .build()));
    }

}
