package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.Currency;

/**
 * Атрибуты операций
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationDTO {
    private String operationId; // Уникальный идентификатор операции
    private String name; // Название операции
    private String iconUrl; // Ссылка на иконку
    private Double sum; // Сумма операции в валюте счета
    private Currency currency; // Валюта счета
}
