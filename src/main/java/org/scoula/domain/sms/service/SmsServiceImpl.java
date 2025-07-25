package org.scoula.domain.sms.service;

import static org.scoula.domain.sms.exception.SmsErrorCode.*;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.member.dto.request.PhoneNumRequest;
import org.scoula.domain.member.dto.request.PhoneVerificationRequest;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

	private static final String SMS_DOMAIN = "https://api.solapi.com";
	private final RedisUtil redisUtil;

	@Value("${coolsms.apiKey}")
	private String apiKey;

	@Value("${coolsms.secretKey}")
	private String apiSecret;

	@Value("${coolsms.caller}")
	private String callerNumber;

	// 인증번호 전송하기
	@Transactional
	@Override
	public void certificateSMS(PhoneNumRequest phoneNumRequest, HttpServletRequest request) {
		DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, SMS_DOMAIN);

		String phoneNumber = phoneNumRequest.phoneNumber().replaceAll("-", "");
		// 랜덤한 인증 번호 생성
		String randomNum = createRandomNumber();

		//인증 번호를 redis에 저장 만료시간은 5분
		redisUtil.set(phoneNumber, randomNum, 5);

		// 발신 정보 설정
		Message message = new Message();
		message.setFrom(callerNumber);
		message.setTo(phoneNumber);
		message.setText("[Dolfin] 인증번호 [" + randomNum + "]를 입력해주세요.");

		try {
			messageService.send(message);
		} catch (NurigoMessageNotReceivedException | NurigoEmptyResponseException | NurigoUnknownException e) {
			throw new CustomException(SMS_SEND_FAIL, LogLevel.ERROR, null, Common.builder()
				.srcIp(request.getRemoteAddr())
				.callApiPath(request.getRequestURI())
				.apiMethod(request.getMethod())
				.deviceInfo(request.getHeader("user-Agent"))
				.build());
		}
	}

	@Override
	public void verifySMS(PhoneVerificationRequest phoneVerificationRequest, HttpServletRequest request) {
		String phoneNumber = phoneVerificationRequest.phoneNumber().replaceAll("-", "");
		String inputCode = phoneVerificationRequest.code();

		// Redis에서 저장된 인증번호 조회
		String savedCode = (String)redisUtil.get(phoneNumber);

		// 인증번호가 없거나 일치하지 않으면 예외 발생
		if (savedCode == null) {
			throw new CustomException(SMS_CODE_EXPIRED, LogLevel.WARNING, null, Common.builder()
				.srcIp(request.getRemoteAddr())
				.callApiPath(request.getRequestURI())
				.apiMethod(request.getMethod())
				.deviceInfo(request.getHeader("user-Agent"))
				.build());
		}

		if (!savedCode.equals(inputCode)) {
			throw new CustomException(SMS_CODE_MISMATCH, LogLevel.WARNING, null, Common.builder()
				.srcIp(request.getRemoteAddr())
				.callApiPath(request.getRequestURI())
				.apiMethod(request.getMethod())
				.deviceInfo(request.getHeader("user-Agent"))
				.build());
		}

		// 인증 성공 시 Redis에서 삭제 (재사용 방지)
		redisUtil.delete(phoneNumber);
	}

	private String createRandomNumber() {
		Random rand = new Random();
		StringBuilder randomNum = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			String random = Integer.toString(rand.nextInt(10));
			randomNum.append(random);
		}

		return randomNum.toString();
	}
}
