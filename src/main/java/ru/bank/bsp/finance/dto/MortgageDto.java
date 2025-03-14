package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.Currency;
import ru.bank.bsp.finance.model.State;
import ru.bank.bsp.finance.model.Type;

/**
 * Массив кредитов (ипотека)
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MortgageDto {
    private String productUid; // Уникальный идентификатор счета/продукта
    private String productId; // Уникальный идентификатор продукта по dboId. Оставлен для обратной совместимости
    private Type type; // Тип кредита
    private State state; // Статус кредита
    private String creditId; // Идентификатор кредита в IBSO
    private String lineId; // Идентификатор кредитной линии в IBSO
    private Double creditAmount; // Начальный размер кредита
    private Double amount; // Размер текущей задолженности
    private String contractNumber; // Номер договора
    private String contractStartDate; // Дата начала кредитного договора
    private String name; // Пользовательское название кредита либо при отсутствии название по умолчанию
    private String repaymentAccountUid; // Уникальный идентификатор счета автосписания
    private String repaymentAccountId; // Уникальный идентификатор счета автосписания по dboId. Оставлен для обратной совместимости
    private Currency currency; // Валюта кредита
    private Double pastDuePrincipalAmount; // Основная часть (тело) текущей задолженности
    private Double debtAmount; // Сумма долга (0 - amount)
    private Double fullAmount; // Доступный остаток
    private SchedulePaymentMortgageDto schedulePayment; // Минимальный/ежемесячный платеж по кредиту
}
