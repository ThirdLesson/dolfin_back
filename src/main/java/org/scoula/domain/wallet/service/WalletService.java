package org.scoula.domain.wallet.service;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.codef.dto.request.WalletRequest;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.wallet.dto.request.ChargeWalletRequest;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.dto.request.TransferToWalletRequest;
import org.scoula.domain.wallet.dto.response.DepositorResponse;
import org.scoula.domain.wallet.dto.response.WalletResponse;
import org.scoula.domain.wallet.entity.Wallet;

public interface WalletService {

	void createWallet(WalletRequest walletRequest, Long memberId, HttpServletRequest request);

	Wallet getWallet(Long walletId, HttpServletRequest request);

	void chargeWallet(ChargeWalletRequest chargeWalletRequest, Long walletId, HttpServletRequest request);

	WalletResponse getWalletByMember(Member member);

	void transferToWallet(TransferToWalletRequest request, Long memberId);

	DepositorResponse getMemberByPhoneNumber(String phoneNumber);

	void transferToAccount(TransferToAccountRequest request, Long memberId);
}
