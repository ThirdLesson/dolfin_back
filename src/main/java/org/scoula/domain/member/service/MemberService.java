package org.scoula.domain.member.service;

import org.scoula.domain.member.dto.MemberDTO;

import java.util.List;

public interface MemberService {
    
    // 회원 전체 조회
    List<MemberDTO> getAllMembers();
    
    // 회원 상세 조회
    MemberDTO getMemberById(Long memberId);
    
    // 로그인 ID로 회원 조회
    MemberDTO getMemberByLoginId(String loginId);
    
    // 회원 등록
    MemberDTO createMember(MemberDTO memberDTO);
    
    // 회원 정보 수정
    MemberDTO updateMember(Long memberId, MemberDTO memberDTO);
    
    // 회원 삭제
    void deleteMember(Long memberId);
    
    // 로그인 ID 중복 체크
    boolean checkLoginIdDuplicate(String loginId);
    
}
