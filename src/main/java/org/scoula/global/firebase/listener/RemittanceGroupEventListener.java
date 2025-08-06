package org.scoula.global.firebase.listener;

import static org.scoula.global.constants.Currency.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.scoula.domain.member.exception.MemberErrorCode;
import org.scoula.domain.member.mapper.MemberMapper;
import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;
import org.scoula.global.firebase.event.RemittanceFailedEvent;
import org.scoula.global.firebase.event.RemittanceGroupChargeNoticeEvent;
import org.scoula.global.firebase.event.RemittanceGroupCompletedEvent;
import org.scoula.global.firebase.event.RemittanceGroupFiredEvent;
import org.scoula.global.firebase.event.RemittanceSuccessEvent;
import org.scoula.global.firebase.util.FirebaseUtil;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.kafka.producer.KafkaProducer;
import org.scoula.global.kafka.util.LogMessageMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RemittanceGroupEventListener {

	private final FirebaseUtil firebaseUtil;
	private final MemberMapper memberMapper;
	private final KafkaProducer kafkaProducer;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleGroupCompleted(RemittanceGroupCompletedEvent event) {
		Long groupId = event.getRemittanceGroupId();

		// 그룹에 속한 멤버들의 FCM 토큰 가져오기
		List<String> fcmTokens = memberMapper.findFcmTokensByRemittanceGroupId(groupId);
		fcmTokens.add(event.getFcmToken());

		// 유효한 토큰만 필터링 (널/빈 문자열 제거)
		List<String> validTokens = fcmTokens.stream()
			.filter(token -> token != null && !token.isBlank())
			.toList();

		if (!validTokens.isEmpty()) {
			firebaseUtil.sendNotices(
				validTokens,
				"단체 송금 혜택이 시작됩니다.",
				"30명이 모여 정기 해외 송금 서비스가 시작됩니다!\n"
					+ "수수료를 포함한 송금 금액을 전자지갑에 미리 충전해주세요.\n"
					+ "송금이 2회 이상 실패할 경우, 혜택 대상에서 자동 제외됩니다.\n"
					+ "※ 국민은행이 외국환은행으로 지정되어 있어야 하며,\n"
					+ "KB스타뱅킹 앱에 가입되어 있어야 합니다."
			);
		}

	}

	@EventListener
	public void handleRemittanceGroupChargeNotice(RemittanceGroupChargeNoticeEvent event) {
		List<MemberWithInformationDto> members = event.getMembers();

		for (MemberWithInformationDto member : members) {
			if (!validToken(member.getFcmToken(), member.getMemberId()))
				continue;

			BigDecimal amountWithCommission = member.getAmount().add(BigDecimal.valueOf(5000));

			if (!member.getFcmToken().isEmpty()) {
				firebaseUtil.sendNotice(
					member.getFcmToken(),
					"정기 송금일이 다가왔습니다.",
					"내일은 해외 정기 송금일입니다.\n" +
						formatAmount(amountWithCommission, KRW) + "원이 전자지갑에 충전되어 있어야 합니다.\n" +
						"충전 금액이 부족할 경우 송금이 실패할 수 있습니다."
				);
			}
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRemittanceGroupFired(RemittanceGroupFiredEvent event) {
		MemberWithInformationDto member = event.getMember();

		if (!validToken(member.getFcmToken(), member.getMemberId()))
			return;

		if (!member.getFcmToken().isEmpty()) {
			firebaseUtil.sendNotice(
				member.getFcmToken(),
				"단체 송금 그룹에서 제외되었습니다.",
				"정기 해외 송금이 2회 실패하여 단체 송금 그룹에서 자동 제외되었습니다.\n" +
					"추후 새로운 그룹에 다시 참여하실 수 있습니다."
			);
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRemittanceFailed(RemittanceFailedEvent event) {
		MemberWithInformationDto member = event.getMember();

		if (!validToken(member.getFcmToken(), member.getMemberId()))
			return;

		if (!member.getFcmToken().isEmpty()) {
			firebaseUtil.sendNotice(
				member.getFcmToken(),
				"단체 송금에 실패하였습니다.",
				"전자지갑 잔액이 부족하여 단체 정기 송금에 실패하였습니다.\n" +
					"한 번 더 실패할 경우, 정기 송금 그룹에서 자동 제외될 수 있습니다."
			);
		}
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleRemittanceSuccess(RemittanceSuccessEvent event) {
		MemberWithInformationDto member = event.getMember();

		if (!validToken(member.getFcmToken(), member.getMemberId()))
			return;

		if (!member.getFcmToken().isEmpty()) {
			firebaseUtil.sendNotice(
				member.getFcmToken(),
				"단체 송금이 완료되었습니다.",
				"우대 환율 30%가 적용된 " + formatAmount(event.getSendAmount(), event.getCurrency()) + "가 전송되었습니다."
			);
		}
	}

	private boolean validToken(String fcmToken, Long memberId) {
		if (fcmToken == null) {
			kafkaProducer.sendToLogTopic(
				LogMessageMapper.buildLogMessage(LogLevel.ERROR, null, MemberErrorCode.FCM_TOKEN_NOT_FOUND.getMessage(),
					Common.builder().build(), "멤버의 fcm 토큰을 찾을 수 없습니다. 멤버 아이디 : " + memberId));
			return false;
		}
		return true;
	}

	private String formatAmount(BigDecimal amount, org.scoula.global.constants.Currency currency) {
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US); // 기본 미국식
		format.setCurrency(Currency.getInstance(currency.name())); // "USD", "KRW", "JPY"
		format.setMinimumFractionDigits(2); // 소수점 자리수
		return format.format(amount);
	}
}
