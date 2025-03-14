package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Беспроцентный период
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GracePeriodDto {
    private String gracePeriodEndDate; // Дата платежа для обеспечения беспроцентного периода
    private Double gracePeriodAmount; // Сумма для обеспечения беспроцентного периода
}
