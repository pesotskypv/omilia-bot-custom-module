package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.Currency;

/**
 * Активы счета
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetsTotalDto {
    private Double amount; // Сумма всех активов
    private Currency currency;
}
