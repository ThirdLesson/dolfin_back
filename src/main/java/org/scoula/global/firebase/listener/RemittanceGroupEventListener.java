package org.scoula.global.firebase.listener;

import java.util.List;

import org.scoula.domain.member.mapper.MemberMapper;
import org.scoula.global.firebase.event.RemittanceGroupCompletedEvent;
import org.scoula.global.firebase.util.FirebaseUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RemittanceGroupEventListener {

	private final FirebaseUtil firebaseUtil;
	private final MemberMapper memberMapper;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleGroupCompleted(RemittanceGroupCompletedEvent event) {
		Long groupId = event.getRemittanceGroupId();

		// 그룹에 속한 멤버들의 FCM 토큰 가져오기
		List<String> fcmTokens = memberMapper.findFcmTokensByRemittanceGroupId(groupId);

		// 유효한 토큰만 필터링 (널/빈 문자열 제거)
		List<String> validTokens = fcmTokens.stream()
			.filter(token -> token != null && !token.isBlank())
			.toList();

		if (!validTokens.isEmpty()) {
			firebaseUtil.sendNotice(
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
}
