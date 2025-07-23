package org.scoula.domain.member.service;

import java.util.List;

import org.scoula.domain.member.dto.MemberDTO;

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
    
}
