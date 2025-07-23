package org.scoula.domain.financialproduct.depositsaving.dto;


import lombok.*;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSaving;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSavingType;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class DepositSavingDTO {
    private final Long depositSavingId;
    private final Long financialCompanyId;
    private final DepositSavingType type;
    private final String name;
    private final String joinWay;
    private final String interestDescription;
    private final String spclCondition;
    private final String ctcNote;
    private final String maxLimit;
    private final Integer saveMonth;
    private final BigDecimal interestRate;
    private final BigDecimal maxInterestRate;
    private final String interestRateType;
    private final String reserveType;

    // DTO → Entity
    public DepositSaving toEntity() {
        return DepositSaving.builder()
                .depositSavingId(this.depositSavingId)
                .financialCompanyId(this.financialCompanyId)
                .type(this.type)
                .name(this.name)
                .joinWay(this.joinWay)
                .interestDescription(this.interestDescription)
                .spclCondition(this.spclCondition)
                .ctcNote(this.ctcNote)
                .maxLimit(this.maxLimit)
                .saveMonth(this.saveMonth)
                .interestRate(this.interestRate)
                .maxInterestRate(this.maxInterestRate)
                .interestRateType(this.interestRateType)
                .reserveType(this.reserveType)
                .build();
    }

    // Entity → DTO
    public static DepositSavingDTO fromEntity(DepositSaving entity) {
        return DepositSavingDTO.builder()
                .depositSavingId(entity.getDepositSavingId())
                .financialCompanyId(entity.getFinancialCompanyId())
                .type(entity.getType())
                .name(entity.getName())
                .joinWay(entity.getJoinWay())
                .interestDescription(entity.getInterestDescription())
                .spclCondition(entity.getSpclCondition())
                .ctcNote(entity.getCtcNote())
                .maxLimit(entity.getMaxLimit())
                .saveMonth(entity.getSaveMonth())
                .interestRate(entity.getInterestRate())
                .maxInterestRate(entity.getMaxInterestRate())
                .interestRateType(entity.getInterestRateType())
                .reserveType(entity.getReserveType())
                .build();
    }
}
