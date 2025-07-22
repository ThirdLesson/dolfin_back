package org.scoula.global.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NationalityCode {
	RUSSIA("0", "러시아"),
	MONGOLIA("1", "몽골"),
	USA("2", "미국"),
	VIETNAM("3", "베트남"),
	INDIA("4", "인도"),
	INDONESIA("5", "인도네시아"),
	JAPAN("6", "일본"),
	CHINA("7", "중국"),
	THAILAND("8", "타이"),
	PHILIPPINES("9", "필리핀"),
	KOREAN_RUSSIAN("10", "한국계 러시아인"),
	KOREAN_CHINESE("11", "한국계 중국인"),
	OTHER("99", "기타");

	private static final Map<String, NationalityCode> BY_CODE =
		Arrays.stream(values()).collect(Collectors.toMap(NationalityCode::getCode, Function.identity()));
	private final String code;
	private final String description;

	public static NationalityCode fromCode(String code) {
		return BY_CODE.getOrDefault(code, OTHER);
	}
}
