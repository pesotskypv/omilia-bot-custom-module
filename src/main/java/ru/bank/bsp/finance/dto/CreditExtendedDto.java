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
public class CreditExtendedDto {
    private String productUid; // Уникальный идентификатор счета/продукта
    private String accountNumber; // 20-значный номер счета (ссудного, погашения или предоставления в зависимости от типа кредита)
    private String branch; // Филиал счета
    private String branchName; // Наименование филиала счета
    private Double amount; // Остаток на счету (доступная сумма для трат для кредитных карт и овердрафтов, текущая задолженность для потребительских кредитов)
    private Double creditAmount; // Лимит овердрафта / Кредитный лимит для кредиток и овердрафтов, первоначальная сумма кредита для потребительских кредитов
    private Double reserveAmount; // Зарезервированные средства
    private Double accruedInterestAmount; // Начисленные проценты на текущий день
    private String contractNumber; // Номер договора
    private String openDate; // Дата открытия счета/продукта
    private String closeDate; // Дата закрытия счета/продукта
    private String name; // Продуктовое наименование кредита
    private String accountTechName; // Техническое наименование счета
    private Currency currency; // Валюта продукта
    private State state; // Статус счета продукта
    private CreditProductType productType; // Тип продукта
    private String accountType; // Технический тип счета в БИС
    private String accountCode; // 13-значный номер счета (ссудного - для потребов, погашения - для овердрафтов, предоставления - для кредиток)
    private Boolean arrest; // Признак ареста
    private String creditAccountCode; // 13-значный номер ссудного счета
    private String repaymentAccountUid; // Уникальный идентификатор счета погашения
    private Double rate; // Процентная ставка
    private String creditCode; // Вид кредита
    private Boolean activity; // Признак активности
    private Double repaymentFullAmount; // Сумма полного досрочного погашения
    private Double repaymentAccountAmount; // Доступный остаток на счете погашения
    private String servicePackage; // Код ПБУ
    private String owner; // Владелец счета
    private Boolean creditProhibition; // Запрет кредитования (приходных операций)
    private Boolean debitProhibition; // Запрет дебетования (расходных операций)
    private Double ownFundsAmount; // Сумма собственных средств
    private Boolean blockedAccount; // Счет заблокирован
    private Boolean overdraft; // По счету есть овердрафт?
    private Boolean primaryFlag; // Признак комиссионного счета
    private String repaymentAccountNumber; // 20-значный номер счета погашения вклада
    private SchedulePaymentExtendedDto schedulePayment; // Минимальный/ежемесячный платеж
    private PastDuePaymentExtendedDto pastDuePayment; // Просрочка для расширенной версии
    private GracePeriodDto gracePeriod; // Беспроцентный период
}
