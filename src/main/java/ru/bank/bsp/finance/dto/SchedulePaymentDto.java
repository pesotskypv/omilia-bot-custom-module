package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Минимальный/ежемесячный платеж по кредиту
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedulePaymentDto {
    private String paymentDate; // Дата платежа или дата расчета следующего платежа по кредитной карте
    private Double fullAmount; // Сумма платежа
}
