package org.scoula.domain.exchange.service.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.exchange.dto.request.ExchangeBankRequest;
import org.scoula.domain.exchange.dto.response.exchangeResponse.BankRateInfo;
import org.scoula.domain.exchange.dto.response.exchangeResponse.ExchangeBankResponse;
import org.scoula.domain.exchange.dto.response.exchangeResponse.PolicyResponse;
import org.scoula.domain.exchange.entity.ExchangeRate;
import org.scoula.domain.exchange.entity.Type;
import org.scoula.domain.exchange.mapper.ExchangeRateMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExchangeRateService {

	private final ExchangeRateMapper exchangeRateMapper;
	private final DecimalFormat currencyFormatter = new DecimalFormat("#,###.##");
	private final DecimalFormat rateFormatter = new DecimalFormat("#,##0.####");

	/**
	 * 각 은행별 기본 환율 계산 (0% 우대율)
	 *
	 * @param request               환율 계산 요청
	 * @param exchangeCommissionFee 환율 수수료
	 * @return 5개 은행의 기본 환율 정보
	 */
	public ExchangeBankResponse calculateExchangeBank(ExchangeBankRequest request,
		BigDecimal amountInUsd,
		BigDecimal exchangeCommissionFee) {

		List<String> banks = List.of("국민은행", "하나은행", "신한은행", "우리은행", "기업은행");
		List<BankRateInfo> rates = new ArrayList<>();

		for (String bank : banks) {
			// BASE 타입 환율 조회 (기준 환율)
			ExchangeRate baseExchange = exchangeRateMapper
				.findLatestExchangeRate(bank, Type.BASE.name(), request.getTargetCurrency());

			// 요청된 타입의 환율 조회 (SEND, GETCASH 등)
			ExchangeRate targetExchangeRate = exchangeRateMapper
				.findLatestExchangeRate(bank, request.getType(), request.getTargetCurrency());

			if (targetExchangeRate == null || baseExchange == null) {
				continue;
			}



			// 기본 환율 계산 (0% 우대)
			BankRateInfo bankRateInfo = calculateBasicRate(targetExchangeRate,
				baseExchange,
				request.getAmount(),
				amountInUsd,
				exchangeCommissionFee);
			rates.add(bankRateInfo);

		}

		// 은행별 정렬 - totalAmount 기준으로 정렬
		sortBanksByAmount(rates, request.getType());

		return ExchangeBankResponse.builder()
			.requestedAmount(request.getAmount())
			.targetCurrency(request.getTargetCurrency())
			.exchangeType(request.getType())
			.banks(rates)
			.build();
	}

	/**
	 * 기본 환율 계산 (0% 우대율, 환율수수료 + 전신료 포함)
	 *
	 * @param targetExchange        목표 환율 (SEND, GETCASH 등)
	 * @param baseExchange          기준 환율 (BASE)
	 * @param amountInKRW           원화 금액
	 * @param exchangeCommissionFee 환율 수수료
	 * @return 기본 환율 정보
	 */
	private BankRateInfo calculateBasicRate(ExchangeRate targetExchange, ExchangeRate baseExchange,
		BigDecimal amountInKRW,
		BigDecimal amountInUsd,
		BigDecimal exchangeCommissionFee) {

		BigDecimal transferFee = getTransferFee(targetExchange.getType(), amountInUsd); // 타입별 전신료

		// 1. 총 수수료 계산 (환율수수료 + 전신료)
		BigDecimal amountAfterFee;
		// 창구 수수료
		BigDecimal amountChangu;
		BigDecimal changuExchangeCommissionFee = calcChanguTransFee(
			targetExchange.getBankName(),
			amountInUsd);

		// 2. 타입에 따라 수수료 처리를 다르게
		if (targetExchange.getType() == Type.SELLCASH ||
			targetExchange.getType() == Type.GETCASH ||
			targetExchange.getType() == Type.BASE) {
			// 현금 거래는 수수료가 환율에 포함되어 있음
			amountAfterFee = amountInKRW;
			amountChangu = amountInKRW;
		} else {
			// 송금/수취는 수수료 별도 차감
			BigDecimal totalFee = exchangeCommissionFee.add(transferFee);
			amountAfterFee = amountInKRW.subtract(totalFee);

			totalFee = changuExchangeCommissionFee.add(transferFee);
			amountChangu = amountInKRW.subtract(totalFee);
		}

		// 환율 적용
		BigDecimal actualRate = targetExchange.getExchangeValue();
		BigDecimal finalAmount = amountAfterFee.divide(actualRate, 2, RoundingMode.HALF_UP);

		// 환율 적용 후 금액 (창구 수수료 적용)
		BigDecimal finalAmountChangu = amountChangu.divide(actualRate, 2, RoundingMode.HALF_UP);

		String finalAmountChanguDisplay = String.format("%s %s",
			currencyFormatter.format(finalAmountChangu.setScale(2, RoundingMode.HALF_UP)),
			targetExchange.getTargetExchange());

		// 3. 환율 표시 문자열
		String rateDisplay = String.format("1 %s 당 %s KRW",
			targetExchange.getTargetExchange(),
			rateFormatter.format(actualRate));

		// 4. 최종 금액 표시 문자열
		String totalAmountDisplay = String.format("%s %s",
			currencyFormatter.format(finalAmount.setScale(2, RoundingMode.HALF_UP)),
			targetExchange.getTargetExchange());

		return BankRateInfo.builder()
			.bankName(targetExchange.getBankName())
			.targetoperation(actualRate)  // 기본 환율 (0% 우대)
			.exchangeRate(rateDisplay)
			.baseoperation(baseExchange.getExchangeValue()) // 기준 환율 (BASE)
			.targetoperation(targetExchange.getExchangeValue()) // 목표 환율 (SEND, GETCASH 등)
			.changguAmount(finalAmountChanguDisplay)
			.totaloperation(finalAmount.setScale(2, RoundingMode.HALF_UP)) // 숫자 값 추가
			.totalAmount(totalAmountDisplay)
			.policyList(new ArrayList<>()) // 빈 리스트로 초기화
			.build();
	}

	/**
	 * 은행별 totaloperation 기준 정렬
	 */
	private void sortBanksByAmount(List<BankRateInfo> banks, String exchangeType) {
		if ("RECEIVE".equals(exchangeType) || "SELLCASH".equals(exchangeType)) {
			// RECEIVE, SELLCASH: 원화를 받기 위해 필요한 외화가 적을수록 좋음 (오름차순)
			banks.sort(Comparator.comparing(BankRateInfo::getTotaloperation));
		} else {
			// SEND, GETCASH, BASE: 원화로 더 많은 외화를 받을수록 좋음 (내림차순)
			banks.sort(Comparator.comparing(BankRateInfo::getTotaloperation).reversed());
		}
	}




	/**
	 * 타입별 전신료 반환
	 *
	 * @param type 거래 타입
	 * @return 전신료
	 */
	private BigDecimal getTransferFee(Type type, BigDecimal amountInUsd) {
		switch (type) {
			case SEND:
				return BigDecimal.valueOf(8000);   			 // 전신료
			case RECEIVE:
				if (amountInUsd.compareTo(BigDecimal.valueOf(100)) < 0) {
					return BigDecimal.ZERO; 				 // 100 uSD 이하 수취 수수료
				} else {
					return BigDecimal.valueOf(10000);		 // 100 USD 초과 수취 수수료
				}
			case SELLCASH:
			case GETCASH:
				return BigDecimal.ZERO;            // 현금은 수수료 없음
			default:
				return BigDecimal.ZERO;
		}
	}

	/**
	 * 은행별 창구 송금 수수료 계산
	 */
	private BigDecimal calcChanguTransFee(
		String bankName,
		BigDecimal usdAmount) {

		BigDecimal exchangeCommissionFee = BigDecimal.ZERO;


		switch (bankName) {
			case "하나은행":
				exchangeCommissionFee = calculateHanaBankFee(usdAmount);
				break;
			case "국민은행":
				exchangeCommissionFee =  calculateKBBankFee(usdAmount);
				break;
			case "기업은행":
				exchangeCommissionFee =  calculateIBKBankFee(usdAmount);
				break;
			case "우리은행":
				exchangeCommissionFee =  calculateWooriBankFee(usdAmount);
				break;
			case "신한은행":
				exchangeCommissionFee =  calculateShinhanBankFee(usdAmount);
				break;
			default:
				exchangeCommissionFee = BigDecimal.ZERO;

		}

		return exchangeCommissionFee;

	}

	/**
	 * 하나은행 송금 수수료 계산
	 */
	// 하나은행 수수료 계산 (창구 기준)
	private BigDecimal calculateHanaBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(5000);    // USD 500 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(10000);   // USD 2천불 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(15000);   // USD 5천불 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(10000)) <= 0) {
			return BigDecimal.valueOf(20000);   // USD 1만불 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(20000)) <= 0) {
			return BigDecimal.valueOf(25000);   // USD 2만불 이하
		} else {
			return BigDecimal.valueOf(25000);   // USD 2만불 초과
		}
	}

	// KB국민은행 수수료 계산 예시
	private BigDecimal calculateKBBankFee(BigDecimal usdAmount) {
		// USD 500달러 상당액 이하	5,000원
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			// USD 2,000달러 상당액 이하	10,000원
			return BigDecimal.valueOf(5000);
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			// USD 2,000달러 상당액 이하	10,000원
			return BigDecimal.valueOf(10000);
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			// USD 5,000달러 상당액 이하	15,000원
			return BigDecimal.valueOf(15000);
		} else if (usdAmount.compareTo(BigDecimal.valueOf(10000)) <= 0) {
			// USD 10,000달러 상당액 이하	20,000원
			return BigDecimal.valueOf(20000);
		} else {
			// USD 10,000달러 상당액 초과	25,000원
			return BigDecimal.valueOf(25000);
		}

	}


	// 신한은행 수수료 계산
	private BigDecimal calculateShinhanBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(5000);    // USD 500 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(10000);   // USD 2,000 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(15000);   // USD 5,000 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(10000)) <= 0) {
			return BigDecimal.valueOf(20000);   // USD 10,000 이하
		} else {
			return BigDecimal.valueOf(25000);   // USD 10,000 초과
		}
	}


	// 기업은행(IBK) 수수료 계산
	private BigDecimal calculateIBKBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(5000);    // USD 500 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(10000);   // USD 2,000 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(15000);   // USD 5,000 이하
		} else {
			return BigDecimal.valueOf(200000);   // USD 20,000 초과
		}
	}

	// 우리은행 수수료 계산
	private BigDecimal calculateWooriBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(5000);    // USD 500 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(10000);   // USD 2,000 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(15000);   // USD 5,000 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(20000)) <= 0) {
			return BigDecimal.valueOf(20000);   // USD 20,000 이하
		} else {
			return BigDecimal.valueOf(25000);   // USD 20,000 초과
		}
	}


}
