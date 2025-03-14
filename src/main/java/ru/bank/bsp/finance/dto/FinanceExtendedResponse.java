package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.ResponseMetaData;

/**
 * Ответ со списком финансовых продуктов
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinanceExtendedResponse {
    private ResponseMetaData result; // Результат запроса получения данных
    private FinanceExtendedDto data; // Список финансовых продуктов
}
