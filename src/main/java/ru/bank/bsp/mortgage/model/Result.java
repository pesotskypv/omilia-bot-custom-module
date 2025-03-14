package ru.bank.bsp.mortgage.model;

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
public class Result {
    private String timestamp; // Дата и время запроса
    private Integer status; // Http-статус ответа
    private String code; // Код результата обработки запроса
    private String message; // Описание кода обработки запроса
}
