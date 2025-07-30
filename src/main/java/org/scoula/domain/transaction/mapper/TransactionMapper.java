package org.scoula.domain.transaction.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.transaction.entity.Transaction;
import org.scoula.domain.transaction.entity.TransactionType;
import org.scoula.global.constants.SortDirection;

@Mapper
public interface TransactionMapper {
	void insert(Transaction transaction);

	// 계좌 거래에 특화된 조회 메서드
	List<Transaction> findByMemberIdAndAccountTransfer(@Param("memberId") Long memberId);

	// 지갑 거래에 특화된 조회 메서드
	List<Transaction> findByMemberIdAndWalletTransfer(@Param("memberId") Long memberId);

	Long countTransactionHistory(
		@Param("memberId") Long memberId,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		@Param("type") TransactionType type,
		@Param("minAmount") BigDecimal minAmount,
		@Param("maxAmount") BigDecimal maxAmount
	);

	List<Transaction> findTransactionHistory(
		@Param("memberId") Long memberId,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		@Param("type") TransactionType type,
		@Param("minAmount") BigDecimal minAmount,
		@Param("maxAmount") BigDecimal maxAmount,
		@Param("sortDirection") SortDirection sortDirection,
		@Param("limit") Integer limit,
		@Param("offset") Integer offset
	);

}
