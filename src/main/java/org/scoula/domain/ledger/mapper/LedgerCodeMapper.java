package org.scoula.domain.ledger.mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.ledger.entity.LedgerCode;

@Mapper
public interface LedgerCodeMapper {
	Optional<LedgerCode> findByName(String name);

	@MapKey("name")
	Map<String, LedgerCode> findByNames(@Param("names") List<String> names);
}
