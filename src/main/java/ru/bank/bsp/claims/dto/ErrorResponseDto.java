package ru.bank.bsp.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Не успешный результат запроса
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
    private String timestamp; // Дата и время сбоя в часовом поясе GMT+3
    private Integer Status; // Http статус ответа
    private String code; // Составной код ошибки вида SERVICE_NAME + NUMERIC_CODE
    private String message; // Описание кода обработки запроса
}
