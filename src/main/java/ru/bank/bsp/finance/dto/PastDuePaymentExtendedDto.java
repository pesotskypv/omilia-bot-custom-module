package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Просрочка для расширенной версии
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PastDuePaymentExtendedDto {
    private Double pastDuePaymentAmount; // Размер просроченного долга
    private String pastDuePaymentDate; // Дата возникновения просроченного долга
    private Boolean pastDuePaymentFlag; // Признак наличия просрочки
}
