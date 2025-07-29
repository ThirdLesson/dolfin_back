package org.scoula.domain.ledger.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.domain.ledger.entity.LedgerVoucher;

@Mapper
public interface LedgerVoucherMapper {
	void insert(LedgerVoucher ledgerVoucher);
}
