package org.scoula.domain.financialproduct.personalcreditloan.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.financialproduct.personalcreditloan.dto.response.PersonalLoanEachResponse;
import org.scoula.domain.financialproduct.personalcreditloan.dto.response.PersonalLoanListResponse;
import org.scoula.domain.financialproduct.personalcreditloan.dto.response.PersonalSearchResponse;
import org.scoula.domain.financialproduct.personalcreditloan.entity.PersonalCreditLoan;
import org.scoula.domain.financialproduct.personalcreditloan.excpetion.PersonalLoanErrorCode;
import org.scoula.domain.financialproduct.personalcreditloan.mapper.PersonalCreditLoanMapper;
import org.scoula.domain.member.entity.Member;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalLoanService {
    
    private final PersonalCreditLoanMapper loanMapper;

    public PersonalLoanListResponse searchLoans(
        String filterType,
        int minAmount,
        int maxAmount,
        Pageable pageable,
        HttpServletRequest httpServletRequest) {


       
        int totalCount = loanMapper.countLoansByConditions(minAmount, maxAmount);

        List<PersonalCreditLoan> loans = loanMapper.findLoansByConditions(
            filterType, minAmount, maxAmount, (int)pageable.getOffset(), pageable.getPageSize()
        );


        List<PersonalSearchResponse> responses = loans.stream()
            .map(loan -> convertToResponse(loan, filterType, httpServletRequest))
            .collect(Collectors.toList());

        return PersonalLoanListResponse.builder()
            .loans(responses)
            .totalCount(totalCount)
            .totalPages((int) Math.ceil((double) totalCount / pageable.getPageSize()))
            .filterType(filterType)
            .build();
    }
    
    private PersonalSearchResponse convertToResponse(PersonalCreditLoan loan, String criteria, HttpServletRequest request) {

        BigDecimal selectedRate = BigDecimal.ZERO;
        switch (criteria) {
            case "default":
                selectedRate = loan.getMaxRate(); 
                break;
            case "MIN_RATE":
                selectedRate = loan.getBaseRate();
                break;
            case "AVG_RATE":
                selectedRate = loan.getSpreadRate(); 
                break;
            case "MAX_RATE":
                selectedRate = loan.getMaxRate(); 
                break;
            default:
                throw new CustomException(PersonalLoanErrorCode.EXCHANGE_NOT_FOUND, LogLevel.ERROR,
                    request.getHeader("txId"),
                    Common.builder()
                        .srcIp(request.getRemoteAddr())
                        .apiMethod(request.getMethod())
                        .callApiPath(request.getRequestURI())
                        .deviceInfo(request.getHeader("user-agent"))
                        .build());
        }
        return PersonalSearchResponse.builder()
            .personalCreditLoanId(loan.getPersonalLoanId())
            .selectedRate(selectedRate)
            .productName(loan.getProductName())
            .companyName(loan.getCompanyName())
            .companyCode(loan.getCompanyCode())
            .build();
    }

    public PersonalLoanEachResponse getLoanDetail(Long id, Member member, HttpServletRequest request) {
        PersonalCreditLoan loan = loanMapper.findById(id);
        if (loan == null) {
            throw new CustomException(PersonalLoanErrorCode.LOAN_NOT_FOUND, LogLevel.ERROR, request.getHeader("txId"),
                Common.builder()
                    .srcIp(request.getRemoteAddr())
                    .apiMethod(request.getMethod())
                    .callApiPath(request.getRequestURI())
                    .deviceInfo(request.getHeader("user-agent"))
                    .build());
        }
        return getLoanEach(loan, member);

    }

    public PersonalLoanEachResponse getLoanEach(PersonalCreditLoan loan, Member member) {
        Integer remainTime = Math.max(0, (int)ChronoUnit.MONTHS.between(LocalDate.now(), member.getRemainTime()));

        Integer visaMinMonths;
        if (loan.getVisaMinMonths() == null|| loan.getVisaMinMonths() < 0) {
            visaMinMonths = 0; 
        } else {
            visaMinMonths = loan.getVisaMinMonths();
        }
        Boolean isForeignerAvailable = false;
        if (remainTime >= visaMinMonths) {
            isForeignerAvailable = true;
        }


        return PersonalLoanEachResponse.builder()
            .personalCreditLoanId(loan.getPersonalLoanId())
            .productName(loan.getProductName())
            .companyName(loan.getCompanyName())
            .companyCode(loan.getCompanyCode())
            .joinAvailable(isForeignerAvailable)
            .maxLoanAmount(loan.getMaxLoanAmount())
            .minRate(loan.getBaseRate())
            .maxRate(loan.getMaxRate())
            .avgRate(loan.getSpreadRate())
            .minPeriod(loan.getMinPeriodMonths())
            .maxPeriod(loan.getMaxPeriodMonths())
            .loanConditions(loan.getLoanConditions())
            .RateRange(loan.getRateInfo())
            .callNumber(loan.getCompanyCallNumber())
            .homepageUrl(loan.getCompanyHomeUrl())
            .build();
    }
}
