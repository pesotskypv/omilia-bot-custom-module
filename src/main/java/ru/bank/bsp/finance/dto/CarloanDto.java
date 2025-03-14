package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.CarloanProductType;
import ru.bank.bsp.finance.model.Currency;
import ru.bank.bsp.finance.model.State;

/**
 * Массив автокредитов с атрибутами до их появления в портфолио
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarloanDto {
    private String productUid; // Уникальный идентификатор счета/продукта
    private String productId; // Уникальный идентификатор продукта по dboId. Оставлен для обратной совместимости
    private Double debtAmount; // Сумма долга
    private Double amount; // Остаток на счету (текущая задолженность для потребительских кредитов)
    private String contractNumber; // Номер договора
    private String openDate; // Дата открытия счета/продукта
    private String closeDate; // Дата закрытия счета/продукта
    private String name; // Пользовательское название счета либо при отсутствии название по умолчанию
    private String repaymentAccountUid; // Уникальный идентификатор счета погашения
    private String repaymentAccountId; // Уникальный идентификатор счета погашения по dboId. Оставлен для обратной совместимости
    private Currency currency; // Валюта кредита
    private State state; // Статус счета продукта
    private CarloanProductType productType; // Тип продукта
    private Double accruedInterestAmount; // Начисленные проценты на текущий день
    private SchedulePaymentDto schedulePayment; // Минимальный/ежемесячный платеж по кредиту
    private PastDuePaymentDto pastDuePayment; // Просрочка
}
