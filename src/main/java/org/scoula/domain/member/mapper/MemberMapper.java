package org.scoula.domain.member.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;

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

	@Update("UPDATE member SET connected_id = #{connectedId} WHERE member_id = #{memberId}")
	void updateConnectedId(@Param("memberId") Long memberId, @Param("connectedId") String connectedId);

	@Update("UPDATE member SET fcm_token = #{fcmToken} WHERE member_id = #{memberId}")
	void updateFcmToken(@Param("memberId") Long memberId, @Param("fcmToken") String fcmToken);

	Optional<Member> selectMemberByPhoneNumber(String phoneNumber);

	List<Member> selectMembersInIds(@Param("memberIds") List<Long> memberIds);

	@Update("UPDATE member SET " +
		"remittance_information_id = #{remittanceInformationId}, " +
		"remittance_group_id = #{remittanceGroupId} " +
		"WHERE member_id = #{memberId}")
	int updateRemittanceRefsStrict(@Param("memberId") Long memberId,
		@Param("remittanceInformationId") Long remittanceInformationId,
		@Param("remittanceGroupId") Long remittanceGroupId);

	@Select("SELECT fcm_token FROM member WHERE remittance_group_id = #{groupId}")
	List<String> findFcmTokensByRemittanceGroupId(@Param("groupId") Long remittanceGroupId);

	@Select("SELECT * FROM member WHERE remittance_group_id = #{groupId}")
	List<Member> findMembersByRemittanceGroupId(@Param("groupId") Long groupId);

	@Select("""
		    SELECT
		        m.member_id,
		        m.remittance_information_id,
		        m.remittance_group_id,
		        m.passport_number,
		        m.nationality,
		        m.country,
		        m.birth,
		        m.name,
		        m.phone_number,
		        m.remain_time,
		        m.currency,
		        m.fcm_token,
		
		        i.receiver_bank,
		        i.swift_code,
		        i.router_code,
		        i.receiver_account,
		        i.receiver_name,
		        i.receiver_nationality,
		        i.receiver_address,
		        i.purpose,
		        i.amount,
		        i.transmit_fail_count,
		        i.intermediary_bank_commission
		
		    FROM member m
		    JOIN remittance_information i ON m.remittance_information_id = i.remittance_information_id
		    WHERE m.remittance_group_id = #{groupId}
		""")
	List<MemberWithInformationDto> findMembersWithInformationByGroupId(@Param("groupId") Long groupId);

}
