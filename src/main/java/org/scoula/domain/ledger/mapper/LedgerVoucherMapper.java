package org.scoula.domain.ledger.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.scoula.domain.ledger.batch.dto.LedgerVoucherWithEntryDTO;
import org.scoula.domain.ledger.entity.LedgerVoucher;

@Mapper
public interface LedgerVoucherMapper {
	void insert(LedgerVoucher ledgerVoucher);

	@Select("""
			SELECT 
				lv.ledger_voucher_id,
				lv.voucher_no,
				lv.entry_date,
				lv.type,
				le.ledger_entry_id,
				le.amount,
				le.ledger_type,
				le.account_code_id
			FROM ledger_voucher lv
			JOIN ledger_entry le ON lv.ledger_voucher_id = le.ledger_voucher_id
			WHERE DATE(lv.entry_date) = CURDATE() - INTERVAL 1 DAY
		""")
	List<LedgerVoucherWithEntryDTO> findAllFromYesterday();
}
