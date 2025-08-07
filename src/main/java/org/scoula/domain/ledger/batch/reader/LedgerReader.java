package org.scoula.domain.ledger.batch.reader;

import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.scoula.domain.transaction.entity.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LedgerReader {

	@Bean
	@StepScope
	public MyBatisPagingItemReader<Transaction> transactionReader(
		@Value("#{stepExecutionContext['startId']}") Long startId,
		@Value("#{stepExecutionContext['endId']}") Long endId,
		@Qualifier("simpleSqlSessionFactory")
		SqlSessionFactory sqlSessionFactory) {
		try {
			MyBatisPagingItemReader<Transaction> reader = new MyBatisPagingItemReader<>();
			reader.setQueryId("org.scoula.domain.transaction.mapper.TransactionMapper.findByTransactionIdRange");
			reader.setParameterValues(Map.of("startId", startId, "endId", endId));
			reader.setSqlSessionFactory(sqlSessionFactory);
			reader.setPageSize(1000000);
			reader.setSaveState(false);
			reader.setName("transactionReader");
			return reader;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
