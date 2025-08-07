package org.scoula.domain.remittancegroup.batch.writer;

import java.util.List;

import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;
import org.scoula.domain.remittancegroup.entity.RemittanceGroup;
import org.scoula.domain.remittancegroup.service.BatchFacadeService;
import org.scoula.domain.wallet.service.WalletService;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RemittanceGroupWriter {

	private final BatchFacadeService batchFacadeService;

	@Bean
	@StepScope
	public ItemWriter<RemittanceGroup> remittanceGroupItemWriter(
		WalletService walletService
	) {
		return items -> {
			for (RemittanceGroup remittanceGroup : items) {

				try {
					List<MemberWithInformationDto> membersWithInformationByGroupId = batchFacadeService.findMembersWithInformationByGroupId(
						remittanceGroup.getRemittanceGroupId());

					for (MemberWithInformationDto memberWithInformationDto : membersWithInformationByGroupId) {
						walletService.remittanceGroup(memberWithInformationDto, remittanceGroup);
					}

				} catch (Exception e) {
					e.printStackTrace();
					log.error("RemittanceGroup 처리 중 오류 발생: {}", remittanceGroup.getRemittanceGroupId(), e);
				}
			}
		};
	}
}
