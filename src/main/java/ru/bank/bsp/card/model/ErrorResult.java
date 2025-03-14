package ru.bank.bsp.card.model;

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
public class ErrorResult {
    private String timestamp;
    private Integer status;
    private String code; // Код результата обработки запроса
    private String message; // Описание кода обработки запроса
    private String debugInfo; // Информация о деталях ошибки сервера
}
