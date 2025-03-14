package ru.bank.bsp.card.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardLimits {
    private Double cashWithdrawalMonthly;
    private Double cashWithdrawalDaily;
    private Double cashWithdrawalToday;
}
