package org.scoula.domain.financialproduct.depositsaving.dto.common;

import java.util.List;

public record ProductDepositDetailsResult(
	List<Integer> savingTerm,
	List<String> spclConditions
) {
}
