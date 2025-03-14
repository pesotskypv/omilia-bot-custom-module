package ru.bank.bsp.statement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.statement.dto.ErrorResponseDto;

/**
 * Не успешный результат запроса
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResult {
    private ErrorResponseDto result;
}
