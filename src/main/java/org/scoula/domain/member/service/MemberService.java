package org.scoula.domain.member.service;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.member.dto.MemberDTO;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;

public interface MemberService {

	// 회원 전체 조회
	List<MemberDTO> getAllMembers();

	// 회원 상세 조회
	MemberDTO getMemberById(Long memberId);

	// 로그인 ID로 회원 조회
	MemberDTO getMemberByLoginId(String loginId);

	MemberDTO updateMember(Long memberId, MemberDTO memberDTO);

	// 회원 삭제
	void deleteMember(Long memberId);

	// 커넥티드 아이디 업데이트
	void updateConnectedId(Long memberId, String connectedId);

	// fcm token 업데이트
	void updateFcmToken(Long memberId, String connectedId);

	Member getMemberByPhoneNumber(String phoneNumber, HttpServletRequest request);

	Optional<List<Member>> getMembersByRemittanceGroup(Long RemittanceGroupId);

	Optional<List<MemberWithInformationDto>> getMemberWithRemittanceInformationByRemittanceGroupId(
		Long RemittanceGroupId);

	void changeRemittanceGroup(List<Long> memberIds, Long toGroupId);

	void decreaseRemittanceGroupMemberCount(Long targetGroupId, int decreaseMemberCount);
}
