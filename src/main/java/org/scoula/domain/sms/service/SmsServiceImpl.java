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

	@Transactional
	@Override
	public void certificateSMS(PhoneNumRequest phoneNumRequest, HttpServletRequest request) {
		DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, SMS_DOMAIN);

		String phoneNumber = phoneNumRequest.phoneNumber().replaceAll("-", "");
		String randomNum = createRandomNumber();

		redisUtil.set(phoneNumber, randomNum, 5);

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

		String savedCode = (String)redisUtil.get(phoneNumber);

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
