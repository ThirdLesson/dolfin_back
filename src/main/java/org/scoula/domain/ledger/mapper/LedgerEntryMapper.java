package org.scoula.domain.ledger.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.domain.ledger.entity.LedgerEntry;

@Mapper
public interface LedgerEntryMapper {
	void insertBatch(List<LedgerEntry> ledgerEntries);
}