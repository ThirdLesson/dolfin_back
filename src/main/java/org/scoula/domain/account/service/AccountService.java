package org.scoula.domain.account.service;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.account.dto.request.CreateAccountRequest;

public interface AccountService {

	void createAccount(CreateAccountRequest createAccountRequest, HttpServletRequest request);
}
