package org.scoula.domain.remittancegroup.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.member.entity.Member;
import org.scoula.domain.remittancegroup.dto.request.JoinRemittanceGroupRequest;
import org.scoula.domain.remittancegroup.dto.response.RemittanceGroupCheckResponse;
import org.scoula.domain.remittancegroup.dto.response.RemittanceGroupCommissionResponse;
import org.scoula.domain.remittancegroup.dto.response.RemittanceGroupMemberCountResponse;
import org.scoula.domain.remittancegroup.entity.RemittanceGroup;
import org.scoula.global.constants.Currency;

public interface RemittanceService {

	RemittanceGroupCommissionResponse getRemittanceGroupCommission(HttpServletRequest request);

	void joinRemittanceGroup(JoinRemittanceGroupRequest joinRemittanceGroupRequest, Member member,
		HttpServletRequest request);

	List<RemittanceGroupMemberCountResponse> getRemittanceGroupMemberCount(Currency currency,
		HttpServletRequest request);

	void RemittanceGroupAlarm();

	void changeRemittanceGroup(RemittanceGroup remittanceGroup);

	RemittanceGroupCheckResponse checkRemittanceGroupExist(Member member);

	void cancelRemittanceGroup(Member member, HttpServletRequest request);
}
