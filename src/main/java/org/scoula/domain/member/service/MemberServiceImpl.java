package org.scoula.domain.member.service;

import static org.scoula.domain.member.exception.MemberErrorCode.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.member.dto.MemberDTO;
import org.scoula.domain.member.dto.response.MemberByPhoneNumberCacheResponse;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.member.mapper.MemberMapper;
import org.scoula.domain.member.util.MemberCache;
import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;
import org.scoula.domain.remittancegroup.mapper.RemittanceGroupMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

	private final MemberMapper memberMapper;
	private final RemittanceGroupMapper remittanceGroupMapper;
	private final MemberCache memberCache;

	@Override
	public List<MemberDTO> getAllMembers() {
		log.info("getAllMembers");
		return memberMapper.selectAllMembers().stream()
			.map(MemberDTO::from)
			.collect(Collectors.toList());
	}

	@Override
	public MemberDTO getMemberById(Long memberId) {
		log.info("getMemberById: {}", memberId);
		Member member = memberMapper.selectMemberById(memberId);

		if (member == null) {
			throw new CustomException(MEMBER_NOT_FOUND, LogLevel.WARNING, null, Common.builder().build());
		}
		return MemberDTO.from(member);
	}

	@Override
	public MemberDTO getMemberByLoginId(String loginId) {
		log.info("getMemberByLoginId: {}", loginId);
		Member member = memberMapper.selectMemberByLoginId(loginId);
		if (member == null) {
			throw new CustomException(MEMBER_NOT_FOUND, LogLevel.WARNING, null, Common.builder().build());
		}
		return MemberDTO.from(member);
	}

	@Override
	@Transactional
	public MemberDTO updateMember(Long memberId, MemberDTO memberDTO) {
		log.info("updateMember: {}", memberId);

		// 기존 회원 정보 조회
		Member existingMember = memberMapper.selectMemberById(memberId);
		if (existingMember == null) {
			throw new CustomException(MEMBER_NOT_FOUND, LogLevel.WARNING, null, Common.builder().build());
		}

		// 수정할 정보 설정
		memberDTO.changePassword(existingMember.getPassword());

		Member member = memberDTO.toEntity();
		memberMapper.updateMember(member);

		return MemberDTO.from(member);
	}

	@Override
	@Transactional
	public void deleteMember(Long memberId) {
		log.info("deleteMember: {}", memberId);

		Member member = memberMapper.selectMemberById(memberId);
		if (member == null) {
			throw new CustomException(MEMBER_NOT_FOUND, LogLevel.WARNING, null, Common.builder().build());
		}

		memberMapper.deleteMember(memberId);
	}

	@Override
	@Transactional
	public void updateConnectedId(Long memberId, String connectedId) {
		memberMapper.updateConnectedId(memberId, connectedId);
	}

	@Override
	@Transactional
	public void updateFcmToken(Long memberId, String fcmToken) {
		memberMapper.updateFcmToken(memberId, fcmToken);
	}

	@Override
	public Member getMemberByPhoneNumber(String phoneNumber, HttpServletRequest request) {

		MemberByPhoneNumberCacheResponse cached = memberCache.get(phoneNumber);
		if (cached != null) {
			return Member.builder()
				.memberId(cached.getMemberId())
				.name(cached.getName())
				.build();
		}

		Member m = memberMapper.selectMemberByPhoneNumber(phoneNumber)
			.orElseThrow(() -> new CustomException(
				MEMBER_NOT_FOUND, LogLevel.WARNING, null,
				Common.builder()
					.apiMethod(request.getMethod())
					.srcIp(request.getRemoteAddr())
					.callApiPath(request.getRequestURI())
					.deviceInfo(request.getHeader("user-agent"))
					.build()
			));

		memberCache.put(phoneNumber,
			MemberByPhoneNumberCacheResponse.builder().memberId(m.getMemberId()).name(m.getName()).build());

		return m;
	}

	@Override
	public Optional<List<Member>> getMembersByRemittanceGroup(Long RemittanceGroupId) {
		return Optional.of(memberMapper.findMembersByRemittanceGroupId(RemittanceGroupId));
	}

	@Override
	public Optional<List<MemberWithInformationDto>> getMemberWithRemittanceInformationByRemittanceGroupId(
		Long RemittanceGroupId) {
		return Optional.of(memberMapper.findMembersWithInformationByGroupId(RemittanceGroupId));
	}

	@Override
	@Transactional
	public void changeRemittanceGroup(List<Long> memberIds, Long toGroupId) {
		if (memberIds == null || memberIds.isEmpty())
			return;
		memberMapper.updateGroupIdByMemberIds(memberIds, toGroupId);
	}

	@Override
	@Transactional
	public void decreaseRemittanceGroupMemberCount(Long targetGroupId, int decreaseMemberCount) {
		remittanceGroupMapper.decreaseMemberCountById(targetGroupId, decreaseMemberCount);
	}

}
