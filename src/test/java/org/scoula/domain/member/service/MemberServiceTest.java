package org.scoula.domain.member.service;

/*

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.domain.member.config.TestMemberConfig;
import org.scoula.domain.member.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestMemberConfig.class})
@Log4j2
@ActiveProfiles("test")  // test 프로파일 활성화
@Transactional  // 테스트 후 롤백
class MemberServiceTest {

	@Autowired
	private MemberService memberService;

	@Test
	public void create() {
		log.info("회원 생성 테스트 시작");

		// Given
		MemberDTO request = MemberDTO.builder()
			.loginId("testuser" + System.currentTimeMillis()) // 중복 방지
			.password("password123")
			.passportNumber("1234567890")
			.nationality("KOR")
			.name("테스트 유저")
			.phoneNumber("010-1234-5678")
			.birth(LocalDate.now())
			.remainTime(LocalDate.now())
			.currency("USD")
			.build();

		// When
		MemberDTO created = memberService.createMember(request);

		// Then
		assertNotNull(created);
		assertNotNull(created.getMemberId());
		assertEquals(request.getLoginId(), created.getLoginId());
		assertEquals(request.getName(), created.getName());

		log.info("회원 생성 완료: {}", created);
		log.info("회원 생성 테스트 완료");
	}



	@Test
	public void getMemberById() {
		log.info("회원 상세 조회 테스트");

		// Given
		MemberDTO request = MemberDTO.builder()
			.loginId("testuser" + System.currentTimeMillis()) // 중복 방지
			.password("password123")
			.passportNumber("1234567890")
			.nationality("KOR")
			.name("테스트 유저")
			.phoneNumber("010-1234-5678")
			.birth(LocalDate.now())
			.remainTime(LocalDate.now())
			.currency("USD")
			.build();

		MemberDTO created = memberService.createMember(request);

		// When
		MemberDTO found = memberService.getMemberById(created.getMemberId());

		// Then
		assertNotNull(found);
		assertEquals(created.getMemberId(), found.getMemberId());
		assertEquals(created.getLoginId(), found.getLoginId());

		log.info("조회된 회원: {}", found);
	}

	@Test
	public void checkLoginIdDuplicate() {
		log.info("로그인 ID 중복 체크 테스트");

		// Given
		MemberDTO request = MemberDTO.builder()
			.loginId("testuser" + System.currentTimeMillis()) // 중복 방지
			.password("password123")
			.passportNumber("1234567890")
			.nationality("KOR")
			.name("테스트 유저")
			.phoneNumber("010-1234-5678")
			.birth(LocalDate.now())
			.remainTime(LocalDate.now())
			.currency("USD")
			.build();

		MemberDTO member = memberService.createMember(request);

		// When & Then
		assertTrue(memberService.checkLoginIdDuplicate(member.getLoginId()));
		assertFalse(memberService.checkLoginIdDuplicate("notexist" + System.currentTimeMillis()));
	}

}
*/
