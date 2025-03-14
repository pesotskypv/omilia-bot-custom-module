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
public class SchedulePaymentMortgageDto {
    private String paymentDate; // Дата следующего платежа
    private Double fullAmount; // Сумма платежа
    private String paymentSettlementDate; // Дата расчета следующего платежа
    private String earlyRepaymentDate; // Дата досрочного погашения
    private Double earlyRepaymentAmount; // Сумма досрочного погашения
}
