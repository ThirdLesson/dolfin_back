package org.scoula.domain.exchange.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.domain.exchange.config.TestConfig;
import org.scoula.domain.exchange.entity.ExchangePolicy;
import org.scoula.domain.exchange.service.policy.ExchangePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource("classpath:test.properties")
@Transactional
@Rollback
@DisplayName("환율 정책 서비스 테스트")
class ExchangePolicyServiceTest {
	@Autowired
	private ExchangePolicyService exchangePolicyService;

	private ExchangePolicy testPolicy;


	@Test
	@DisplayName("서비스 Bean이 정상적으로 주입되는지 테스트")
	void testServiceInjection() {
		assertNotNull(exchangePolicyService, "ExchangePolicyService가 정상적으로 주입되어야 합니다");
		System.out.println("✅ 서비스 Bean 주입 성공: " + exchangePolicyService.getClass().getSimpleName());
	}

	// @Test
	// @DisplayName("모든 활성 정책 조회 테스트")
	// void testGetAllActivePolicies() {
	// 	System.out.println("🔍 모든 활성 정책 조회 테스트 시작");
	//
	// 	// When
	// 	List<PolicyOption> policyOptions = exchangePolicyService.applyExchangePolicies(
	// 		"국민은행", // 국민은행
	// 		"USD", // 모든 통화
	// 		"SEND", // 모든 타입
	// 		true  // 활성 정책만 조회
	// 	);
	// 	// Then
	// 	System.out.println("✅ 활성 정책 조회 성공 - 조회된 정책 수: " + policyOptions.size());
	// 	for (PolicyOption policyOption : policyOptions) {
	// 		System.out.printf("🏦 정책명: %s, 우대율: %s%%, 수수료: %s원, 전화 환율: %s원%n",
	// 			policyOption.getPolicyName(),
	// 			policyOption.getExchangeDiscountRate(),
	// 			policyOption.getExchangeCommissionFee(),
	// 			policyOption.getBaseTelephoneFee()
	// 		);
	// 	}
	// }
/*
	@Test
	@DisplayName("USD SEND 정책 조회 테스트")
	void testGetUSDSendPolicies() {
		System.out.println("🔍 USD SEND 정책 조회 테스트 시작");

		// When
		List<ExchangePolicy> usdSendPolicies = exchangePolicyService.getPoliciesByTargetCurrencyAndType("USD", Type.SEND);

		// Then
		assertNotNull(usdSendPolicies);
		System.out.println("✅ USD SEND 정책 조회 성공 - 조회된 정책 수: " + usdSendPolicies.size());

		// USD SEND 정책들 출력
		usdSendPolicies.forEach(policy -> {
			System.out.printf("🏦 %s: %s (우대: %s%%)%n",
				policy.getBankName(),
				policy.getPolicyName(),
				policy.getExchangeDiscountRate()
			);
		});
	}

	@Test
	@DisplayName("KB국민은행 USD SEND 정책 조회 테스트")
	void testGetKBUSDSendPolicies() {

		// When
		List<ExchangePolicy> kbPolicies = exchangePolicyService.getPoliciesByBankAndCurrencyAndType("KB국민은행", "USD", Type.SEND);

		// Then
		assertNotNull(kbPolicies);
		System.out.println("✅ KB국민은행 USD SEND 정책 조회 성공 - 조회된 정책 수: " + kbPolicies.size());

		// KB국민은행 정책들 출력
		kbPolicies.forEach(policy -> {
			System.out.printf("📋 %s (우대: %s%%, 전신료: %s원)%n",
				policy.getPolicyName(),
				policy.getExchangeDiscountRate(),
				policy.getTelegraphFee()
			);
		});
	}

	*/


/*
	@Test
	@DisplayName("KB국민은행 최고 우대율 정책 조회 테스트")
	void testGetBestKBPolicy() {
		System.out.println("🔍 KB국민은행 최고 우대율 정책 조회 테스트 시작");

		// When
		ExchangePolicy bestPolicy = exchangePolicyService.getBestPolicyByBankAndCurrency("KB국민은행", "USD", Type.SEND);

		// Then
		if (bestPolicy != null) {
			System.out.println("✅ KB국민은행 최고 우대율 정책 조회 성공");
			System.out.printf("🏆 최고 정책: %s (우대: %s%%, 전신료: %s원)%n",
				bestPolicy.getPolicyName(),
				bestPolicy.getExchangeDiscountRate(),
				bestPolicy.getTelegraphFee()
			);
		} else {
			System.out.println("⚠️ KB국민은행 USD SEND 정책이 없습니다");
		}
	}

	@Test
	@DisplayName("정책 생성 테스트")
	void testCreatePolicy() {
		System.out.println("🔍 정책 생성 테스트 시작");

		// When
		ExchangePolicy createdPolicy = exchangePolicyService.createPolicy(testPolicy);

		// Then
		assertNotNull(createdPolicy);
		assertNotNull(createdPolicy.getPolicyId());
		assertEquals(testPolicy.getBankName(), createdPolicy.getBankName());
		assertEquals(testPolicy.getTargetCurrency(), createdPolicy.getTargetCurrency());
		assertEquals(testPolicy.getExchangeType(), createdPolicy.getExchangeType());

		System.out.println("✅ 정책 생성 성공 - ID: " + createdPolicy.getPolicyId());
	}

	@Test
	@DisplayName("정책 조회 by ID 테스트")
	void testGetPolicyById() {
		// Given
		ExchangePolicy createdPolicy = exchangePolicyService.createPolicy(testPolicy);
		System.out.println("🔍 정책 ID로 조회 테스트 시작 - ID: " + createdPolicy.getPolicyId());

		// When
		ExchangePolicy foundPolicy = exchangePolicyService.getPolicyById(createdPolicy.getPolicyId());

		// Then
		assertNotNull(foundPolicy);
		assertEquals(createdPolicy.getPolicyId(), foundPolicy.getPolicyId());
		assertEquals(createdPolicy.getBankName(), foundPolicy.getBankName());

		System.out.println("✅ 정책 조회 성공: " + foundPolicy.getPolicyName());
	}

	@Test
	@DisplayName("유효하지 않은 파라미터 검증 테스트")
	void testInvalidParameters() {
		System.out.println("🔍 유효하지 않은 파라미터 검증 테스트 시작");

		// null 통화
		assertThrows(IllegalArgumentException.class, () -> {
			exchangePolicyService.getPoliciesByTargetCurrencyAndType(null, Type.SEND);
		});

		// null 타입
		assertThrows(IllegalArgumentException.class, () -> {
			exchangePolicyService.getPoliciesByTargetCurrencyAndType("USD", null);
		});

		// null 은행명
		assertThrows(IllegalArgumentException.class, () -> {
			exchangePolicyService.getPoliciesByBankAndCurrencyAndType(null, "USD", Type.SEND);
		});

		System.out.println("✅ 유효성 검증 성공");
	}

	*/
}
