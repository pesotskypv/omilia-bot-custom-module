package ru.bank.bsp.mortgage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.mortgage.model.Result;

/**
 * Детальная информация по кредиту
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MortgageDetailsResult {
    private Result result; // Метаинформация по запросу
    private MortgageDetailsDto data; // Детальная информация по кредиту
}
