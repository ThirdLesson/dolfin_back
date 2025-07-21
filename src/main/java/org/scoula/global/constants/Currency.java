package org.scoula.global.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
	USD("미국", "달러"),
	JPY("일본", "엔"),
	EUR("유럽연합", "유로"),
	GBP("영국", "파운드"),
	CAD("캐나다", "달러"),
	KRW("한국", "원"),
	HKD("홍콩", "달러"),
	CNY("중국", "위안"),
	THB("태국", "바트"),
	IDR("인도네시아", "루피아"),
	VND("베트남", "동"),
	RUB("러시아", "루블"),
	BDT("방글라데시", "타카"),
	;

	private final String country;
	private final String unit;

}
