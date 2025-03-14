package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * Просрочка
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PastDuePaymentDto {
    private Double pastDuePaymentAmount; // Размер просроченного долга
    private String pastDuePaymentDate; // Дата возникновения просроченного долга
}
