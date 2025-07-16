package org.scoula.domain.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.member.entity.Member;

@Mapper
public interface MemberMapper {
    
    // 회원 전체 조회
    List<Member> selectAllMembers();
    
    // 회원 상세 조회
    Member selectMemberById(@Param("memberId") Long memberId);
    
    // 로그인 ID로 회원 조회
    Member selectMemberByLoginId(@Param("loginId") String loginId);
    
    // 회원 등록
    int insertMember(Member member);
    
    // 회원 정보 수정
    int updateMember(Member member);
    

    // 회원 삭제
    int deleteMember(@Param("memberId") Long memberId);
    
    // 로그인 ID 중복 체크
    int checkLoginIdDuplicate(@Param("loginId") String loginId);
}
