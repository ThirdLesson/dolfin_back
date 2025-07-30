package org.scoula.domain.account.entity;

import lombok.Getter;

@Getter
public enum BankType {
	산업은행("0002"),
	기업은행("0003"),
	국민은행("0004"),
	수협은행("0007"),
	농협은행("0011"),
	우리은행("0020"),
	제일은행("0023"),
	씨티은행("0027"),
	대구은행("0031"),
	부산은행("0032"),
	광주은행("0034"),
	제주은행("0035"),
	전북은행("0037"),
	경남은행("0039"),
	새마을금고("0045"),
	신협은행("0048"),
	우체국("0071"),
	하나은행("0081"),
	신한은행("0088"),
	케이뱅크("0089");

	private final String code;

	BankType(String code) {
		this.code = code;
	}
}
