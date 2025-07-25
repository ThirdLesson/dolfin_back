package org.scoula.domain.sms.service;

import org.scoula.domain.member.dto.request.PhoneNumRequest;
import org.scoula.domain.member.dto.request.PhoneVerificationRequest;

public interface SmsService {
	void certificateSMS(PhoneNumRequest phoneNumber);

	void verifySMS(PhoneVerificationRequest phoneVerificationRequest);
}
