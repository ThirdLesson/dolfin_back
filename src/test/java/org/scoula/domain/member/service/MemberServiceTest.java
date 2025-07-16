package org.scoula.domain.member.service;


import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.domain.member.config.TestMemberConfig;
import org.scoula.domain.member.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestMemberConfig.class})
@Log4j2
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
			.birth(new Date())
			.remainTime(new Date())
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
	public void getList() {
		log.info("회원 목록 조회 테스트");

		memberService.getAllMembers().forEach(member -> {
			log.info(member);
		});
	}

	@Test
	public void getMemberById() {
		log.info("회원 상세 조회 테스트");

		// Given - 먼저 회원 생성
		MemberDTO newMember = MemberDTO.builder()
			.loginId("testuser" + System.currentTimeMillis())
			.password("password123")
			.name("조회 테스트")
			.build();

		MemberDTO created = memberService.createMember(newMember);

		// When
		MemberDTO found = memberService.getMemberById(created.getMemberId());

		// Then
		assertNotNull(found);
		assertEquals(created.getMemberId(), found.getMemberId());
		assertEquals(created.getLoginId(), found.getLoginId());

		log.info("조회된 회원: {}", found);
	}

	@Test
	public void updateMember() {
		log.info("회원 수정 테스트");

		// Given - 회원 생성
		MemberDTO newMember = MemberDTO.builder()
			.loginId("updatetest" + System.currentTimeMillis())
			.password("password123")
			.name("수정 전")
			.phoneNumber("010-1111-1111")
			.build();

		MemberDTO created = memberService.createMember(newMember);

		// When - 수정
		MemberDTO updateData = MemberDTO.builder()
			.name("수정 후")
			.phoneNumber("010-2222-2222")
			.nationality("USA")
			.build();

		MemberDTO updated = memberService.updateMember(created.getMemberId(), updateData);

		// Then
		assertEquals("수정 후", updated.getName());
		assertEquals("010-2222-2222", updated.getPhoneNumber());
		assertEquals("USA", updated.getNationality());

		log.info("수정된 회원: {}", updated);
	}

	@Test
	public void checkLoginIdDuplicate() {
		log.info("로그인 ID 중복 체크 테스트");

		// Given
		String loginId = "duplicate" + System.currentTimeMillis();
		MemberDTO member = MemberDTO.builder()
			.loginId(loginId)
			.password("password123")
			.name("중복 테스트")
			.build();

		memberService.createMember(member);

		// When & Then
		assertTrue(memberService.checkLoginIdDuplicate(loginId));
		assertFalse(memberService.checkLoginIdDuplicate("notexist" + System.currentTimeMillis()));
	}

	@Test
	public void login() {
		log.info("로그인 테스트");

		// Given
		String loginId = "logintest" + System.currentTimeMillis();
		String password = "password123";

		MemberDTO member = MemberDTO.builder()
			.loginId(loginId)
			.password(password)
			.name("로그인 테스트")
			.build();

		memberService.createMember(member);

		// When - 정상 로그인
		MemberDTO loggedIn = memberService.login(loginId, password);

		// Then
		assertNotNull(loggedIn);
		assertEquals(loginId, loggedIn.getLoginId());
		assertNull(loggedIn.getPassword()); // 비밀번호는 제거되어야 함

		// When & Then - 잘못된 비밀번호
		assertThrows(RuntimeException.class, () -> {
			memberService.login(loginId, "wrongpassword");
		});
	}
}
