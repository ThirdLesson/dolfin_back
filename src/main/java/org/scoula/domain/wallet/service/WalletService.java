package org.scoula.domain.wallet.service;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.codef.dto.request.WalletRequest;
import org.scoula.domain.wallet.dto.response.WalletResponse;

public interface WalletService {

	void createWallet(WalletRequest walletRequest, Long memberId, HttpServletRequest request);

	WalletResponse getWalletByMemberId(Long MemberId);
}
