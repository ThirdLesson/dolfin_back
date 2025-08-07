package org.scoula.domain.financialproduct.constants;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SavingSpclConditionType {
	ONLINE("비대면 가입", "online", List.of("비대면", "온라인", "인터넷")),
	BANK_APP("은행앱 사용", "bankApp", List.of("앱", "스마트", "모바일앱")),
	USING_SALARY_ACCOUNT("급여 연동", "usingSalaryAccount", List.of("급여", "월급")),
	USING_UTILITY_BILL("공과금 연동", "usingUtilityBill", List.of("공과금", "자동납부")),
	USING_CARD("카드 사용", "usingCard", List.of("카드", "신용카드")),
	FIRST_BANKING("첫거래", "firstBanking", List.of("첫거래", "신규", "첫")),
	DEPOSIT_ACCOUNT("입출금통장", "depositAccount", List.of("입출금", "당행", "당행 계좌")),
	PENSION("연금 관련", "pension", List.of("연금")),
	AUTO_DEPOSIT("자동이체", "autoDeposit", List.of("자동이체", "자동 이체")),
	RECOMMEND_COUPON("추천코드", "recommendCoupon", List.of("추천", "쿠폰")),
	HOUSING_SUBSCRIPTION("청약", "housingSubscription", List.of("청약")),

	// 매핑 실패 시 기본값
	ETC("기타", "etc", List.of());

	private final String koreanName;
	private final String apiValue;
	private final List<String> keywords;

	// 한글 이름 기준 매핑
	public static SavingSpclConditionType fromKoreanName(String koreanName) {
		String normalized = koreanName.replaceAll("\\s+", "");
		for (SavingSpclConditionType condition : values()) {
			if (condition.koreanName.replaceAll("\\s+", "").equals(normalized)) {
				return condition;
			}
		}
		return null;
	}

	// API 값 기준 매핑
	public static SavingSpclConditionType fromApiValue(String apiValue) {
		for (SavingSpclConditionType condition : values()) {
			if (condition.apiValue.equalsIgnoreCase(apiValue)) {
				return condition;
			}
		}
		System.out.println("enum 매핑 실패 - API value: " + apiValue);
		return null;
	}

	public static SavingSpclConditionType fromDescription(String description) {
		if (description == null || description.isBlank()) {
			return ETC;
		}

		String normalized = description.replaceAll("\\s+", "").toLowerCase();

		// 키워드 기반 매핑
		for (SavingSpclConditionType condition : values()) {
			for (String keyword : condition.keywords) {
				if (normalized.contains(keyword.toLowerCase())) {
					return condition;
				}
			}
		}

		// Fallback
		return ETC;
	}

	// 영어 enum 이름 기준 매핑
	public static SavingSpclConditionType fromEnglishName(String englishName) {
		try {
			return SavingSpclConditionType.valueOf(englishName);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	// 화면 출력용
	public String getDisplayName() {
		return this.koreanName;
	}

	// DB 저장용
	public String getDbValue() {
		return this.name();
	}
}
