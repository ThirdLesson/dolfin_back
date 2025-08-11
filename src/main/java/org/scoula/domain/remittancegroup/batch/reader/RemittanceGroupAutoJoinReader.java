package org.scoula.domain.remittancegroup.batch.reader;

import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.scoula.domain.remittancegroup.entity.RemittanceGroup;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemittanceGroupAutoJoinReader {

	@Bean
	@StepScope
	public MyBatisPagingItemReader<RemittanceGroup> autoJoinReader(
		@Value("#{stepExecutionContext['startId']}") Long startId,
		@Value("#{stepExecutionContext['endId']}") Long endId,
		@Qualifier("simpleSqlSessionFactory")
		SqlSessionFactory sqlSessionFactory) {
		MyBatisPagingItemReader<RemittanceGroup> reader = new MyBatisPagingItemReader<>();

		reader.setQueryId(
			"org.scoula.domain.remittancegroup.mapper.RemittanceGroupMapper.findByIdRangeBenefitStatusOn");
		reader.setParameterValues(Map.of("startId", startId, "endId", endId));
		reader.setSqlSessionFactory(sqlSessionFactory);
		reader.setPageSize(1000);
		reader.setSaveState(false);
		reader.setName("autoJoinReader");

		return reader;
	}
}
