package org.scoula.domain.remittancegroup.service;

import java.util.List;

import org.scoula.domain.member.mapper.MemberMapper;
import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BatchFacadeService {

	private final MemberMapper memberMapper;

	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public List<MemberWithInformationDto> findMembersWithInformationByGroupId(Long groupId) {
		return memberMapper.findMembersWithInformationByGroupId(groupId);
	}
}
