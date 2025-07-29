package org.scoula.domain.account.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.account.dto.request.CreateAccountRequest;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.account.dto.response.AccountListResponse;

public interface AccountService {

	void createAccount(CreateAccountRequest createAccountRequest, HttpServletRequest request);

	void depositToAccount(TransferToAccountRequest request);

	List<AccountListResponse> getAllAccountsByWalletId(Long walletId, HttpServletRequest request);
}
