package ru.bank.bsp.claims.model;

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
    private String timestamp; // Дата и время сбоя в часовом поясе GMT+3
    private Integer status; // Http статус ответа
    private String id;
    private String code; // Составной код ошибки вида SERVICE_NAME + NUMERIC_CODE
    private String message; // Описание кода обработки запроса
}
