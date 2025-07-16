package org.scoula.domain.member.service;

import static org.scoula.domain.member.exception.MemberErrorCode.*;
import static org.scoula.global.exception.errorCode.CommonErrorCode.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.domain.member.dto.MemberDTO;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.member.mapper.MemberMapper;
import org.scoula.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

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
            throw new CustomException(MEMBER_NOT_FOUND);
        }
        return MemberDTO.from(member);
    }

    @Override
    public MemberDTO getMemberByLoginId(String loginId) {
        log.info("getMemberByLoginId: {}", loginId);
        Member member = memberMapper.selectMemberByLoginId(loginId);
        if (member == null) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }
        return MemberDTO.from(member);
    }

    @Override
    @Transactional
    public MemberDTO createMember(MemberDTO memberDTO) {
        log.info("createMember: {}", memberDTO.getLoginId());

        // 로그인 ID 중복 체크
        if (checkLoginIdDuplicate(memberDTO.getLoginId())) {
            throw new CustomException(MEMBER_ALREADY_EXISTS);
        }

        // 임시로 비밀번호를 그대로 저장 (나중에 암호화 추가)
        Member member = memberDTO.toEntity();
        memberMapper.insertMember(member);

        return MemberDTO.from(member);
    }
    @Override
    @Transactional
    public MemberDTO updateMember(Long memberId, MemberDTO memberDTO) {
        log.info("updateMember: {}", memberId);

        // 기존 회원 정보 조회
        Member existingMember = memberMapper.selectMemberById(memberId);
        if (existingMember == null) {
            throw new CustomException(MEMBER_NOT_FOUND);
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
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        memberMapper.deleteMember(memberId);
    }

    @Override
    public boolean checkLoginIdDuplicate(String loginId) {
        log.info("checkLoginIdDuplicate: {}", loginId);
        return memberMapper.checkLoginIdDuplicate(loginId) > 0;
    }

}
