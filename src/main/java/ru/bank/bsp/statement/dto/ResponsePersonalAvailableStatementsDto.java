package ru.bank.bsp.statement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.statement.model.ResponseMetaData;

/**
 * Получение списка доступных для формирования справок
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePersonalAvailableStatementsDto {
    ResponseMetaData result;
    PersonalAvailableStatementsDto data; // Информация о доступных справках
}
