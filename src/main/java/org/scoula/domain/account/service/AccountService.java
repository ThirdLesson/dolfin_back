package org.scoula.domain.account.service;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.account.dto.request.CreateAccountRequest;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;

public interface AccountService {

	void createAccount(CreateAccountRequest createAccountRequest, HttpServletRequest request);

	void depositToAccount(TransferToAccountRequest request);
}
