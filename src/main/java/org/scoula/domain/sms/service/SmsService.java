package org.scoula.domain.sms.service;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.member.dto.request.PhoneNumRequest;
import org.scoula.domain.member.dto.request.PhoneVerificationRequest;

public interface SmsService {
	void certificateSMS(PhoneNumRequest phoneNumber, HttpServletRequest request);

	void verifySMS(PhoneVerificationRequest phoneVerificationRequest, HttpServletRequest request);
}
