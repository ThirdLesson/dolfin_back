package org.scoula.domain.wallet.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.codef.dto.request.WalletRequest;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.wallet.dto.request.ChargeWalletRequest;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.dto.request.TransferToWalletRequest;
import org.scoula.domain.wallet.dto.response.DepositorResponse;
import org.scoula.domain.wallet.dto.response.RecentAccountReceiversResponse;
import org.scoula.domain.wallet.dto.response.RecentWalletReceiversResponse;
import org.scoula.domain.wallet.dto.response.WalletResponse;
import org.scoula.domain.wallet.entity.Wallet;

public interface WalletService {

	void createWallet(WalletRequest walletRequest, Long memberId, HttpServletRequest request);

	Wallet getWallet(Long walletId, HttpServletRequest request);

	void chargeWallet(ChargeWalletRequest chargeWalletRequest, Long walletId, HttpServletRequest request);

	WalletResponse getWalletByMember(Member member, HttpServletRequest request);

	void transferToWallet(TransferToWalletRequest request, Member member, HttpServletRequest servletRequest);

	DepositorResponse getMemberByPhoneNumber(String phoneNumber, HttpServletRequest request);

	void transferToAccount(TransferToAccountRequest request, Long memberId, HttpServletRequest servletRequest);

	List<RecentAccountReceiversResponse> getRecentAccountReceivers(Member member);

	List<RecentWalletReceiversResponse> getRecentWalletReceivers(Member member);
}
