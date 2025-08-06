package org.scoula.domain.remittancegroup.batch.reader;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;

import org.scoula.domain.remittancegroup.entity.RemittanceGroup;
import org.scoula.domain.remittancegroup.mapper.RemittanceGroupMapper;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@StepScope
@RequiredArgsConstructor
public class RemittanceGroupReader implements ItemReader<RemittanceGroup> {

	private final RemittanceGroupMapper remittanceGroupMapper;

	@Value("#{stepExecutionContext['startId']}")
	private Long startId;

	@Value("#{stepExecutionContext['endId']}")
	private Long endId;

	private Iterator<RemittanceGroup> delegate;

	@Override
	public RemittanceGroup read() throws Exception {
		if (delegate == null) {
			int day = LocalDate.now(ZoneId.of("Asia/Seoul")).getDayOfMonth();
			List<RemittanceGroup> data = remittanceGroupMapper.findByIdRange(startId, endId, day);
			delegate = data.iterator();
		}

		return delegate.hasNext() ? delegate.next() : null;
	}
}
