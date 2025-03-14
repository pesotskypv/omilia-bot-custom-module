package ru.bank.bsp.claims.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.claims.dto.ErrorResponseDto;

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
