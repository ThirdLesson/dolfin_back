package org.scoula.domain.financialproduct.personalcreditloan.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.*;

@RestController
@RequestMapping("/api/personal-credit-loans")
@Slf4j
public class PersonalCreditLoanController {

	public PersonalCreditLoanController() {
		log.info("==================== PersonalCreditLoanController 생성됨! ====================");
		System.out.println("==================== PersonalCreditLoanController 생성됨! ====================");
	}

	@GetMapping("/test")
	public String test() {
		return "PersonalCreditLoanController is working!";
	}
}
