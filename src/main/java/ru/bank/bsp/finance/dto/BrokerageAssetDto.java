package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.BrokerageAssetType;
import ru.bank.bsp.finance.model.Currency;

/**
 * Активы счета
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrokerageAssetDto {
    private BrokerageAssetType type; // Тип актива
    private Double amount; // Сумма актива
    private Currency currency;
    private Double portfolioPercentage; // Процентное соотношение актива к портфелю
}
