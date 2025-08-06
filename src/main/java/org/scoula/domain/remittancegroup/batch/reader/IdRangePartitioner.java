package org.scoula.domain.remittancegroup.batch.reader;

import java.util.HashMap;
import java.util.Map;

import org.scoula.domain.remittancegroup.mapper.RemittanceGroupMapper;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class IdRangePartitioner implements Partitioner {

	private final RemittanceGroupMapper remittanceGroupMapper;

	public IdRangePartitioner(RemittanceGroupMapper remittanceGroupMapper) {
		this.remittanceGroupMapper = remittanceGroupMapper;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Long minId = remittanceGroupMapper.findMinIdBenefitStatusOn();
		Long maxId = remittanceGroupMapper.findMaxIdBenefitStatusOn();

		Map<String, ExecutionContext> result = new HashMap<>();

		if (minId == null || maxId == null) {
			return result;
		}

		long targetSize = (maxId - minId) / gridSize + 1;
		long start = minId;
		long end = start + targetSize - 1;

		for (int i = 0; i < gridSize; i++) {
			ExecutionContext context = new ExecutionContext();
			context.putLong("startId", start);
			context.putLong("endId", Math.min(end, maxId));
			result.put("partition" + i, context);
			start += targetSize;
			end += targetSize;
		}

		return result;
	}
}
