package org.scoula.domain.remittancegroup.entity;

import org.scoula.global.constants.Currency;
import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 단체 송금
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RemittanceGroup extends BaseEntity {

	private Long remittanceGroupId;         // 송금 그룹 아이디 (PK)
	private BenefitStatus benefitStatus; // 혜택 적용 상태
	private Integer remittanceDate; // 정기 송금 날짜
	private Integer memberCount; // 참여중인 사람 수
	private Currency currency; // 설정 통화

}

