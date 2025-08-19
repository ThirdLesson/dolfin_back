package org.scoula.domain.member.service;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.member.dto.MemberDTO;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;

public interface MemberService {

	List<MemberDTO> getAllMembers();

	MemberDTO getMemberById(Long memberId);

	MemberDTO getMemberByLoginId(String loginId);

	MemberDTO updateMember(Long memberId, MemberDTO memberDTO);

	void deleteMember(Long memberId);

	void updateConnectedId(Long memberId, String connectedId);

	void updateFcmToken(Long memberId, String connectedId);

	Member getMemberByPhoneNumber(String phoneNumber, HttpServletRequest request);

	Optional<List<Member>> getMembersByRemittanceGroup(Long RemittanceGroupId);

	Optional<List<MemberWithInformationDto>> getMemberWithRemittanceInformationByRemittanceGroupId(
		Long RemittanceGroupId);

	void changeRemittanceGroup(List<Long> memberIds, Long toGroupId);

	void decreaseRemittanceGroupMemberCount(Long targetGroupId, int decreaseMemberCount);
}
