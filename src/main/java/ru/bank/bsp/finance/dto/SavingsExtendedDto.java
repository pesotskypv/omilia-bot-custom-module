package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.Currency;
import ru.bank.bsp.finance.model.SavingsProductType;
import ru.bank.bsp.finance.model.State;

/**
 * Массив текущих, сберегательных и депозитных счетов
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsExtendedDto {
    private String productUid; // Уникальный идентификатор счета/продукта
    private String accountNumber; // 20-значный номер счета
    private String accountCode; // 13-значный номер счета
    private String branch; // Филиал счета
    private String branchName; // Наименование филиала счета
    private Double amount; // Остаток на счету
    private Double reserveAmount; // Зарезервированные средства
    private Double creditAmount; // Лимит овердрафта / Кредитный лимит
    private String openDate; // Дата открытия счета/продукта
    private String closeDate; // Дата закрытия счета/продукта
    private String name; // Наименование продукта по умолчанию
    private String accountTechName; // Техническое наименование счета
    private Currency currency; // Валюта счета
    private State state; // Статус счета
    private SavingsProductType productType; // Тип продукта
    private String accountType; // Технический тип счета в БИС
    private Boolean creditProhibition; // Запрет кредитования (приходных операций)
    private Boolean debitProhibition; // Запрет дебетования (расходных операций)
    private Boolean arrest; // Признак ареста
    private Double maxAmount; // Максимальная допустимая сумма на счете
    private Double ownFundsAmount; // Сумма собственных средств
    private String servicePackage; // Код ПБУ
    private String owner; // Владелец счета
    private Boolean blockedAccount; // Счет заблокирован
    private Boolean overdraft; // По счету есть овердрафт?
    private Boolean primaryFlag; // Признак комиссионного счета
    private String contractNumber; // Номер договора вклада
    private Double initialAmount; // Первоначальная сумма вклада
    private Double initialRate; // Начальная ставка по вкладу
    private Double rate; // Текущая ставка по вкладу
    private String repaymentAccountNumber; // 20-значный номер счета погашения вклада
}
