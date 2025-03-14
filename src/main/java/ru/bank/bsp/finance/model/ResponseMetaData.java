package ru.bank.bsp.finance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Результат запроса получения данных
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMetaData {
    private String timestamp;
    private Integer status; // Http статус ответа
    private String code; // Код результата обработки запроса
    private String message; // Описание кода обработки запроса
}
