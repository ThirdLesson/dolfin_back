package org.scoula.domain.transaction.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.domain.transaction.entity.Transaction;

@Mapper
public interface TransactionMapper {
	void insert(Transaction transaction);
}
