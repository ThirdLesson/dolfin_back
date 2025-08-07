package org.scoula.domain.ledger.batch.reader;

import java.util.HashMap;
import java.util.Map;

import org.scoula.domain.transaction.mapper.TransactionMapper;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class LedgerIdRangePartitioner implements Partitioner {

	private final TransactionMapper transactionMapper;

	public LedgerIdRangePartitioner(TransactionMapper transactionMapper) {
		this.transactionMapper = transactionMapper;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Long minId = transactionMapper.findMinIdFromYesterday();
		Long maxId = transactionMapper.findMaxIdFromYesterday();

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
