package org.scoula.domain.ledger.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.domain.ledger.entity.LedgerCode;

@Mapper
public interface LedgerCodeMapper {
	Optional<LedgerCode> findByName(String name);
}
