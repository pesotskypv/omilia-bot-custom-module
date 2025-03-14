package ru.bank.bsp.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.card.model.LimitType;
import ru.bank.bsp.card.model.OperationType;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardLimitResponseDto {
    private OperationType operationType;
    private LimitType limitType;
    private String currencyCode;
    private Double maxLimitAmount;
    private Double limitAmount;
    private Double expendedAmount;
}
