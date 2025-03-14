package ru.bank.bsp.statement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.statement.model.ResponseMetaData;

import java.util.List;

/**
 * Получение информации по заказанным справкам
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseListStatementDto {
    private ResponseMetaData result;
    private List<StatementDto> data;
}
