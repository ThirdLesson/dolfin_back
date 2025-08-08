package org.scoula.domain.financialproduct.personalcreditloan.controller;

import javax.servlet.http.HttpServletRequest;
import org.scoula.domain.financialproduct.personalcreditloan.dto.response.PersonalLoanEachResponse;
import org.scoula.domain.financialproduct.personalcreditloan.dto.response.PersonalLoanListResponse;
import org.scoula.domain.financialproduct.personalcreditloan.service.PersonalLoanService;
import org.scoula.global.response.SuccessResponse;
import org.scoula.global.security.dto.CustomUserDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/personal-loans")

@RequiredArgsConstructor
@Api(tags = "개인 대출 상품 API")
public class PersonalLoanController {
    
    private final PersonalLoanService loanService;

    @GetMapping("/recommend")
    @ApiOperation(value = "개인 대출 상품 리스트 금리 - 선택한  낮은순 ")
    public SuccessResponse<PersonalLoanListResponse> searchLoans(
        @RequestParam(required = false, defaultValue = "AVG_RATE") String filterType,
        @RequestParam(defaultValue = "0") int minAmount,
        @RequestParam(defaultValue = "1000000000") int maxAmount,
        @PageableDefault(
            size = 20,
            sort ="maxInterestRate",
            direction = Sort.Direction.DESC
        ) Pageable pageable,
        HttpServletRequest httpServletRequest) {


        return SuccessResponse.ok(
            loanService.searchLoans(filterType, minAmount, maxAmount, pageable, httpServletRequest));
    }

    @GetMapping("/{personalLoanId}")
    @ApiOperation(value = "개인 대출 상품 단건 조회")
    public SuccessResponse<PersonalLoanEachResponse> getLoanDetail(@PathVariable Long personalLoanId,
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        HttpServletRequest httpServletRequest) {
        return SuccessResponse.ok(loanService.getLoanDetail(personalLoanId, customUserDetails.getMember(),
            httpServletRequest));
    }



}
