package org.scoula.domain.financialproduct.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DepositSpclCondition {
	비대면가입("online"),
	은행앱사용("bankApp"),
	급여연동("usingSalaryAccount"),
	연금("pension"),
	공과금_연동("usingUtilityBill"),
	카드_사용("usingCard"),
	첫거래("firstBanking"),
	입출금통장("depositAccount"),
	재예치("depositAgain");

	private String value;

	public static DepositSpclCondition fromValue(String value) {
		for (DepositSpclCondition condition : values()) {
			if (condition.value.equals(value)) {
				return condition;
			}
		}
		return null;
	}
}
