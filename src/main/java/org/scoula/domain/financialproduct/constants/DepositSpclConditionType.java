package org.scoula.domain.financialproduct.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DepositSpclConditionType {
	ONLINE("비대면 가입", "online"),
	BANK_APP("은행앱 사용", "bankApp"),
	USING_SALARY_ACCOUNT("급여 연동", "usingSalaryAccount"),
	PENSION("연금", "pension"),
	USING_UTILITY_BILL("공과금 연동", "usingUtilityBill"),
	USING_CARD("카드 사용", "usingCard"),
	FIRST_BANKING("첫거래", "firstBanking"),
	DEPOSIT_ACCOUNT("입출금통장", "depositAccount"),
	DEPOSIT_AGAIN("재예치", "depositAgain");

	private final String koreanName;
	private final String apiValue;

	public static DepositSpclConditionType fromKoreanName(String koreanName) {
		String normalized = koreanName.replaceAll("\\s+", "");
		for (DepositSpclConditionType condition : values()) {
			if (condition.koreanName.replaceAll("\\s+", "").equals(normalized)) {
				return condition;
			}
		}
		return null;
	}

	public static DepositSpclConditionType fromApiValue(String apiValue) {
		for (DepositSpclConditionType condition : values()) {
			if (condition.apiValue.equalsIgnoreCase(apiValue)) {
				return condition;
			}
		}
		System.out.println("❗ enum 매핑 실패 - API value: " + apiValue);
		return null;
	}

	public static DepositSpclConditionType fromEnglishName(String englishName){
		try{
			return DepositSpclConditionType.valueOf(englishName);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	public String getDisplayName(){
		return this.koreanName;
	}
	public String getDbValue(){
		return this.name();
	}
	public String getApiValue() {return this.apiValue;}
}
