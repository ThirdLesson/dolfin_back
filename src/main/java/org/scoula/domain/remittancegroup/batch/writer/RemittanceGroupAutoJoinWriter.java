package org.scoula.domain.remittancegroup.batch.writer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.scoula.domain.remittancegroup.entity.RemittanceGroup;
import org.scoula.domain.remittancegroup.service.RemittanceService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RemittanceGroupAutoJoinWriter {

	private final RemittanceService remittanceService;

	@Bean("remittanceGroupAutoJoinItemWriter")
	public ItemWriter<RemittanceGroup> remittanceGroupAutoJoinItemWriter() {
		return items -> {
			List<RemittanceGroup> sorted = new ArrayList<>(items);
			sorted.sort(Comparator.comparingInt(g ->
				g.getMemberCount() != null ? g.getMemberCount() : 0
			));

			if (log.isDebugEnabled()) {
				for (RemittanceGroup g : sorted) {
					log.debug("memberCount={}", g.getMemberCount());
				}
			}

			for (RemittanceGroup g : sorted) {
				if (g.getMemberCount() != null && g.getMemberCount() >= 30)
					continue;

				remittanceService.changeRemittanceGroup(g);
			}
		};
	}
}
