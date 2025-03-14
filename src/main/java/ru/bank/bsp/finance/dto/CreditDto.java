package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.CreditProductType;
import ru.bank.bsp.finance.model.Currency;
import ru.bank.bsp.finance.model.State;

/**
 * Массив кредитов и счетов кредитных карт / овердрафтов
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditDto {
    private String productUid; // Уникальный идентификатор счета/продукта
    private String productId; // Уникальный идентификатор продукта по dboId. Оставлен для обратной совместимости
    private String branch; // Филиал счета
    private Double accountAmount; // Сумма на счете
    private Double debtAmount; // Сумма долга
    private Double debtUnauthorizedOverdraftAmount; // Задолженность по неавторизованному овердрафту
    private Double overdraftLimitAmount; // Доступный лимит овердрафта
    private Double fullAmount; // Остаток на счету (задолженность для потребительских кредитов, доступная сумма для трат для кредитных карт и овердрафтов). Суммарный баланс счета для отображения в переводах
    private Double amount; // Остаток на счету (доступная сумма для трат для кредитных карт и овердрафтов, текущая задолженность для потребительских кредитов)
    private Double creditAmount; // Лимит овердрафта / Кредитный лимит для кредиток и овердрафтов, первоначальная сумма кредита для потребительских кредитов
    private Double reserveAmount; // Зарезервированные средства
    private Double accruedInterestAmount; // Начисленные проценты на текущий день
    private String contractNumber; // Номер договора
    private String openDate; // Дата открытия счета/продукта
    private String closeDate; // Дата закрытия счета/продукта
    private String name; // Пользовательское название счета либо при отсутствии название по умолчанию
    private String accountCode; // 13-значный номер счета (ссудного - для потребов, погашения - для овердрафтов, предоставления - для кредиток)
    private String creditAccountCode; // 13-значный номер ссудного счета
    private String creditAccountNumber; // 20-значный номер ссудного счета
    private String repaymentAccountUid; // Уникальный идентификатор счета погашения
    private String repaymentAccountId; // Уникальный идентификатор счета погашения по dboId. Оставлен для обратной совместимости
    private Currency currency; // Валюта кредита
    private Double creditLimit; // Кредитный лимит
    private Double rate; // Процентная ставка
    private Double cashRate; // Процентная ставка на кэш операции
    private String paymentsFrequency; // Частота выплат
    private State state; // Статус счета продукта
    private CreditProductType productType; // Тип продукта
    private SchedulePaymentDto schedulePayment; // Минимальный/ежемесячный платеж по кредиту
    private PastDuePaymentDto pastDuePayment; // Просрочка
    private GracePeriodDto gracePeriod; // Беспроцентный период
}
