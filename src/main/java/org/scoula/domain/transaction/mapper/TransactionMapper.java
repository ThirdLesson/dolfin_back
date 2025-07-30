package org.scoula.domain.transaction.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.transaction.entity.Transaction;

@Mapper
public interface TransactionMapper {
	void insert(Transaction transaction);

	// 계좌 거래에 특화된 조회 메서드
	List<Transaction> findByMemberIdAndAccountTransfer(@Param("memberId") Long memberId);

	// 지갑 거래에 특화된 조회 메서드
	List<Transaction> findByMemberIdAndWalletTransfer(@Param("memberId") Long memberId);

}
