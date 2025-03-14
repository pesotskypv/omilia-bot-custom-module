package ru.bank.bsp.mortgage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.mortgage.model.MortgageCurrency;
import ru.bank.bsp.mortgage.model.MortgageState;

/**
 * Атрибуты счёта обслуживания кредита
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalAccountDto {
    private String id; // Идентификатор счёта
    private String name; // Название счёта
    private Double amount; // Остаток на счёте
    private MortgageCurrency currency; // Валюта счёта
    private MortgageState state; // Статус счёта
}
