package ru.bank.bsp.tariff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Неуспешный результат запроса
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResultDto {
    private String timestamp;
    private Integer status; // Http статус ответа
    private Integer id; // Идентификатор объекта из запроса, при его наличии
    private String code; // Код результата обработки запроса
    private String message; // Описание кода обработки запроса
    private String debugInfo; // Информация о деталях ошибки сервера
    private Result result;
}
