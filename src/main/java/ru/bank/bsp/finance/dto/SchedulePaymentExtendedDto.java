package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Минимальный/ежемесячный платеж
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchedulePaymentExtendedDto {
    private String nextPaymentDate; // Дата платежа
    private String paymentSettlementDate; // Дата расчета следующего платежа по кредитной карте
    private Double fullAmount; // Сумма платежа
}
