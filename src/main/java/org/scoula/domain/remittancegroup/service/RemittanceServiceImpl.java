package org.scoula.domain.remittancegroup.service;

import static org.scoula.domain.remittancegroup.exception.RemittanceGroupErrorCode.*;
import static org.scoula.global.constants.Currency.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.exchange.entity.ExchangeRate;
import org.scoula.domain.exchange.entity.Type;
import org.scoula.domain.exchange.mapper.ExchangeRateMapper;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.member.mapper.MemberMapper;
import org.scoula.domain.member.service.MemberService;
import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;
import org.scoula.domain.remittancegroup.dto.request.JoinRemittanceGroupRequest;
import org.scoula.domain.remittancegroup.dto.response.RemittanceGroupCheckResponse;
import org.scoula.domain.remittancegroup.dto.response.RemittanceGroupCommissionResponse;
import org.scoula.domain.remittancegroup.dto.response.RemittanceGroupMemberCountResponse;
import org.scoula.domain.remittancegroup.entity.BenefitStatus;
import org.scoula.domain.remittancegroup.entity.RemittanceGroup;
import org.scoula.domain.remittancegroup.mapper.RemittanceGroupMapper;
import org.scoula.domain.remmitanceinformation.entity.RemittanceInformation;
import org.scoula.domain.remmitanceinformation.mapper.RemittanceInformationMapper;
import org.scoula.global.constants.Currency;
import org.scoula.global.exception.CustomException;
import org.scoula.global.firebase.event.RemittanceGroupChangedEvent;
import org.scoula.global.firebase.event.RemittanceGroupChargeNoticeEvent;
import org.scoula.global.firebase.event.RemittanceGroupCompletedEvent;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RemittanceServiceImpl implements RemittanceService {

	// 국민은행의 기본 외국 송금 수수료 정책
	private static final BigDecimal TRANSMIT_COMMISSION = BigDecimal.valueOf(3000);
	private static final BigDecimal INTERMEDIARY_COMMISSION = BigDecimal.valueOf(16.5); // 16.5 달러 * 현재 환율 (곱셈 필요함)
	private static final BigDecimal TELEGRAPHIC_TRANSFER_FEE = BigDecimal.valueOf(5000);
	private static final String BANK_NAME = "하나은행";
	private static final String TARGET_CURRENCY = "USD";

	private static final BigDecimal REMITTANCE_COMMISSION = BigDecimal.valueOf(
		5000); // 수수료가 제일 저렴한 quick send 상품의 수수료 모델 참고

	private final ExchangeRateMapper exchangeRateMapper;
	private final RemittanceGroupMapper remittanceGroupMapper;
	private final RemittanceInformationMapper remittanceInformationMapper;
	private final MemberMapper memberMapper;
	private final ApplicationEventPublisher eventPublisher;
	private final MemberService memberService;

	@Override
	@Transactional(readOnly = true)
	public RemittanceGroupCommissionResponse getRemittanceGroupCommission(HttpServletRequest request) {
		ExchangeRate latestExchangeRate = exchangeRateMapper.findLatestExchangeRate(BANK_NAME, Type.BASE.name(),
			TARGET_CURRENCY);

		if (latestExchangeRate == null) {
			latestExchangeRate = new ExchangeRate(null, null, null, BigDecimal.valueOf(1365), null, null, null);
		}

		BigDecimal multiply = INTERMEDIARY_COMMISSION.multiply(latestExchangeRate.getExchangeValue());
		BigDecimal originalCommission = TELEGRAPHIC_TRANSFER_FEE.add(TRANSMIT_COMMISSION).add(multiply);
		BigDecimal benefitAmount = originalCommission.subtract(REMITTANCE_COMMISSION).abs();

		return new RemittanceGroupCommissionResponse(originalCommission, REMITTANCE_COMMISSION, benefitAmount);
	}

	@Override
	public void joinRemittanceGroup(JoinRemittanceGroupRequest joinRemittanceGroupRequest, Member member,
		HttpServletRequest request) {
		// validateRemittanceGroup(member, request);
		Optional<RemittanceGroup> remittanceGroup = remittanceGroupMapper.findByCurrencyAndBenefitStatusAndRemittanceDate(
			joinRemittanceGroupRequest.currency(), BenefitStatus.OFF, joinRemittanceGroupRequest.remittanceDate()
		);

		// 기존 그룹이 없을 경우 분기 처리
		try {
			if (remittanceGroup.isEmpty()) {
				RemittanceGroup newGroup = createNewRemittanceGroup(joinRemittanceGroupRequest, BenefitStatus.OFF);
				remittanceGroupMapper.insert(newGroup);

				RemittanceInformation information = createNewRemittanceInformation(joinRemittanceGroupRequest);
				remittanceInformationMapper.insert(information);

				memberMapper.updateRemittanceRefsStrict(member.getMemberId(), information.getRemittanceInformationId(),
					newGroup.getRemittanceGroupId());

				return;
			}
		} catch (DuplicateKeyException e) {
			Optional<RemittanceGroup> existRemittanceGroup = remittanceGroupMapper.findByCurrencyAndBenefitStatusAndRemittanceDate(
				joinRemittanceGroupRequest.currency(), BenefitStatus.OFF, joinRemittanceGroupRequest.remittanceDate()
			);
			existGroup(existRemittanceGroup, member, joinRemittanceGroupRequest);
		}

		// 기존 그룹이 존재할 경우
		existGroup(remittanceGroup, member, joinRemittanceGroupRequest);

	}

	@Override
	@Transactional(readOnly = true)
	public List<RemittanceGroupMemberCountResponse> getRemittanceGroupMemberCount(Currency currency,
		HttpServletRequest request) {

		List<RemittanceGroup> allByCurrencyAndBenefitStatusOff =
			remittanceGroupMapper.findAllByCurrencyAndBenefitStatusOff(currency);

		// 날짜별 그룹 수 map 으로 저장
		Map<Integer, Integer> dateToCountMap = new HashMap<>();
		for (RemittanceGroup group : allByCurrencyAndBenefitStatusOff) {
			dateToCountMap.put(group.getRemittanceDate(), group.getMemberCount());
		}

		// 1~31일 날짜 기준으로 리스트 생성 (없는 날짜는 0명 처리)
		List<RemittanceGroupMemberCountResponse> memberCountResponses = new ArrayList<>();
		for (int day = 1; day <= 31; day++) {
			int count = dateToCountMap.getOrDefault(day, 0);
			memberCountResponses.add(new RemittanceGroupMemberCountResponse(day, count));
		}

		return memberCountResponses;
	}

	@Transactional(readOnly = true)
	@Override
	public void RemittanceGroupAlarm() {
		int day = LocalDate.now(ZoneId.of("Asia/Seoul")).getDayOfMonth() + 1;
		List<RemittanceGroup> byDayBenefitOn = remittanceGroupMapper.findByDayBenefitOn(day);

		List<Long> groupIds = byDayBenefitOn.stream()
			.map(RemittanceGroup::getRemittanceGroupId)
			.collect(Collectors.toList());

		if (groupIds.isEmpty())
			return;

		List<MemberWithInformationDto> membersWithInfoByGroupIds = memberMapper.findMembersWithInfoByGroupIds(groupIds);

		eventPublisher.publishEvent(new RemittanceGroupChargeNoticeEvent(membersWithInfoByGroupIds));

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void changeRemittanceGroup(RemittanceGroup remittanceGroup) {
		int chargeMemberCount = 30 - remittanceGroup.getMemberCount();

		Currency targetCurrency = remittanceGroup.getCurrency();
		int day = remittanceGroup.getRemittanceDate();

		// 동일 currency, day 새 그룹에서 인원 빼와서 충전하기
		Optional<RemittanceGroup> remittanceGroupByCurrencyAndDate = remittanceGroupMapper.findByDayAndCurrencyAndBenefitOFF(
			day, targetCurrency);
		if (remittanceGroupByCurrencyAndDate.isEmpty())
			return;

		RemittanceGroup newlyRemittanceGroup = remittanceGroupByCurrencyAndDate.get();

		List<Long> changedMemberIds = new ArrayList<>();

		// 현재 모집 중인 그룹의 멤버 수가 채워야하는 인원의 수보다 적으면 전부 다 데려와서 채워버리기
		if (newlyRemittanceGroup.getMemberCount() <= chargeMemberCount) {
			Optional<List<Member>> membersByRemittanceGroup = memberService.getMembersByRemittanceGroup(
				newlyRemittanceGroup.getRemittanceGroupId());

			if (membersByRemittanceGroup.isEmpty())
				return;
			List<Member> newlyGroupMembers = membersByRemittanceGroup.get();
			changedMemberIds = newlyGroupMembers.stream()
				.map(Member::getMemberId)
				.collect(Collectors.toList());

			memberService.changeRemittanceGroup(changedMemberIds, remittanceGroup.getRemittanceGroupId());

			remittanceGroupMapper.increaseMemberCountById(remittanceGroup.getRemittanceGroupId(),
				newlyRemittanceGroup.getMemberCount());
			memberService.decreaseRemittanceGroupMemberCount(newlyRemittanceGroup.getRemittanceGroupId(),
				newlyRemittanceGroup.getMemberCount());
		} else {
			// 현재 모집 중인 그룹의 멤버 수가 채워야하는 인원의 수보다 많을 경우
			// 같은 통화 같은 날짜의 사람들 조회하기 + information 조인
			Optional<List<MemberWithInformationDto>> memberWithRemittanceInformationByRemittanceGroupId = memberService.getMemberWithRemittanceInformationByRemittanceGroupId(
				newlyRemittanceGroup.getRemittanceGroupId());

			if (memberWithRemittanceInformationByRemittanceGroupId.isEmpty())
				return;
			List<MemberWithInformationDto> memberWithInformationDtos = memberWithRemittanceInformationByRemittanceGroupId.get();
			// 송금 information의 updated_at을 기준으로 오름차순 정렬 시키기
			memberWithInformationDtos.sort((o1, o2) -> o1.getUpdatedAt().compareTo(o2.getUpdatedAt()));

			// 가장 먼저 가입한 사람부터 chargeNumber 만큼 짤라서 아이디 List 만들고 송금 그룹 외래키 업데이트
			int changedCnt = 0;
			for (int i = 0; i < memberWithInformationDtos.size(); i++) {
				changedMemberIds.add(memberWithInformationDtos.get(i).getMemberId());
				changedCnt++;
				if (changedCnt == chargeMemberCount) {
					break;
				}
			}
			memberService.changeRemittanceGroup(changedMemberIds, remittanceGroup.getRemittanceGroupId());

			remittanceGroupMapper.increaseMemberCountById(remittanceGroup.getRemittanceGroupId(), changedCnt);
			memberService.decreaseRemittanceGroupMemberCount(newlyRemittanceGroup.getRemittanceGroupId(),
				changedCnt);
		}

		eventPublisher.publishEvent(new RemittanceGroupChangedEvent(changedMemberIds));
	}

	@Override
	public RemittanceGroupCheckResponse checkRemittanceGroupExist(Member member) {

		if (member.getRemittanceGroupId() == null) {
			return RemittanceGroupCheckResponse.builder()
				.groupExists(false)
				.remittanceGroupId(null)
				.groupCurrency(null)
				.memberCount(0)
				.build();
		}

		RemittanceGroup remittanceGroup = remittanceGroupMapper.findById(member.getRemittanceGroupId());

		if (remittanceGroup == null) {
			return RemittanceGroupCheckResponse.builder()
				.groupExists(false)
				.remittanceGroupId(null)
				.groupCurrency(null)
				.memberCount(0)
				.build();
		}


		return RemittanceGroupCheckResponse.builder()
			.groupExists(true)
			.remittanceGroupId(remittanceGroup.getRemittanceGroupId())
			.groupCurrency(remittanceGroup.getCurrency().name())
			.memberCount(remittanceGroup.getMemberCount())
			.build();
	}

	@Override
	public void cancelRemittanceGroup(Member member, HttpServletRequest request) {
		if (member.getRemittanceGroupId() == null) {
			throw new CustomException(NOT_EXIST_GROUP,
				LogLevel.WARNING, null,
				Common.builder()
					.deviceInfo(request.getHeader("user-agent"))
					.srcIp(request.getRemoteAddr())
					.apiMethod(request.getMethod())
					.callApiPath(request.getRequestURI())
					.memberId(member.getMemberId().toString())
					.build());
		}

		// 그룹이 존재할 경우
		RemittanceGroup remittanceGroup = remittanceGroupMapper.findById(member.getRemittanceGroupId());
		if (remittanceGroup == null) {
			throw new CustomException(NOT_EXIST_GROUP,
				LogLevel.WARNING, null,
				Common.builder()
					.deviceInfo(request.getHeader("user-agent"))
					.srcIp(request.getRemoteAddr())
					.apiMethod(request.getMethod())
					.callApiPath(request.getRequestURI())
					.memberId(member.getMemberId().toString())
					.build());
		}

		int memberCount = remittanceGroup.getMemberCount();

		if (memberCount <= 0) {
			throw new CustomException(MEMBER_COUNT_NUMBER_ZERO, LogLevel.WARNING, null,
				Common.builder()
					.deviceInfo(request.getHeader("user-agent"))
					.srcIp(request.getRemoteAddr())
					.apiMethod(request.getMethod())
					.callApiPath(request.getRequestURI())
					.memberId(member.getMemberId().toString())
					.build());
		}

		remittanceGroupMapper.decreaseMemberCountById(remittanceGroup.getRemittanceGroupId(), 1);

		remittanceInformationMapper.deleteById(member.getRemittanceInformationId());

		memberMapper.updateRemittanceRefsStrict(member.getMemberId(), null, null);

	}

	private void validateRemittanceGroup(Member member, HttpServletRequest request) {
		if (member.getRemittanceGroupId() != null) {
			throw new CustomException(ALREADY_JOINED_GROUP, LogLevel.WARNING, null,
				Common.builder()
					.deviceInfo(request.getHeader("user-agent"))
					.srcIp(request.getRemoteAddr())
					.apiMethod(request.getMethod())
					.callApiPath(request.getRequestURI())
					.memberId(member.getMemberId().toString())
					.build());
		}
	}

	private void existGroup(Optional<RemittanceGroup> remittanceGroup, Member member,
		JoinRemittanceGroupRequest joinRemittanceGroupRequest) {
		// 기존 그룹이 존재할 경우
		RemittanceGroup existGroup = remittanceGroup.get();
		int newMemberCount = existGroup.getMemberCount() + 1;
		remittanceGroupMapper.updateMemberCountById(existGroup.getRemittanceGroupId(), newMemberCount);

		RemittanceInformation information = createNewRemittanceInformation(joinRemittanceGroupRequest);
		remittanceInformationMapper.insert(information);

		memberMapper.updateRemittanceRefsStrict(member.getMemberId(), information.getRemittanceInformationId(),
			existGroup.getRemittanceGroupId());

		// 그룹 멤버 30명이 다 모였을 경우
		if (newMemberCount == 30) {
			remittanceGroupMapper.updateBenefitStatusOnById(existGroup.getRemittanceGroupId());
			// 트랜잭션 종료 후 알림 전송을 위한 이벤트 발행
			eventPublisher.publishEvent(
				new RemittanceGroupCompletedEvent(existGroup.getRemittanceGroupId(), member.getFcmToken()));
		}
	}

	private RemittanceGroup createNewRemittanceGroup(JoinRemittanceGroupRequest joinRemittanceGroupRequest,
		BenefitStatus benefitStatus) {
		return RemittanceGroup.builder()
			.remittanceDate(joinRemittanceGroupRequest.remittanceDate())
			.benefitStatus(benefitStatus)
			.memberCount(1)
			.currency(joinRemittanceGroupRequest.currency())
			.build();
	}

	private RemittanceInformation createNewRemittanceInformation(
		JoinRemittanceGroupRequest joinRemittanceGroupRequest) {
		RemittanceInformation information = RemittanceInformation.builder()
			.amount(joinRemittanceGroupRequest.amount())
			.purpose(joinRemittanceGroupRequest.purpose())
			.receiverAccount(joinRemittanceGroupRequest.receiverAccount())
			.receiverAddress(joinRemittanceGroupRequest.receiverAddress())
			.receiverBank(joinRemittanceGroupRequest.receiverBank())
			.receiverName(joinRemittanceGroupRequest.receiverName())
			.receiverNationality(joinRemittanceGroupRequest.currency().getCountry())
			.intermediaryBankCommission(joinRemittanceGroupRequest.intermediaryBankCommission())
			.transmitFailCount(0)
			.swiftCode(joinRemittanceGroupRequest.swiftCode())
			.build();

		if (joinRemittanceGroupRequest.currency().equals(USD)) {
			information.updateRouterCode(joinRemittanceGroupRequest.routerCode());
		}

		return information;
	}
}
