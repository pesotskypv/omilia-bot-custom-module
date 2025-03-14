package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.ResponseMetaData;

/**
 * Ответ со списком финансовых продуктов для внешних каналов
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseNewFinanceDto {
    private ResponseMetaData result; // Результат запроса получения данных
    private FinanceDto data; // Список финансовых продуктов
}
