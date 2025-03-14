package ru.bank.bsp.tariff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommissionParametersDto {
    private String mainValue; // Основное значение комиссии
    private String additionalValue; // Дополнительное значение комиссии
    private String operationType; // Тип операции
}
