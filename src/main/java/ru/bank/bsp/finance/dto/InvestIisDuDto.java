package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.Currency;
import ru.bank.bsp.finance.model.State;

/**
 * Продукты Axiom ИИС ДУ
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestIisDuDto {
    private String productUid; // Уникальный идентификатор продукта
    private String productId; // Идентификатор продукта
    private Currency currency; // Валюта договора
    private String strategyName; // Наименование стратегии
    private String dateSigning; // Дата подписания договора
    private State state; // Cтатус договора. ACTIVE - активный, CLOSED - закрыт
    private Double amount; // Сумма в валюте договора
    private Double equivalent; // Сумма договора в рублях
    private Double finResult; // Финансовый результат
    private Double yield; // Финансовый результат в процентном выражении
    private String dateCalculation; // Дата расчета стоимости активов
    private String updatedAt; // Дата обновления
    private Boolean isStopOperations; // Признак приостановки операций по продукту
    private String dateStopOperations; // Дата обновления
    private String refillAccountDetailsBankName; // Наименование банка получателя
    private String refillAccountDetailsBankCity; // Город банка-получателя
    private String refillAccountDetailsBankBic; // Бик банка-получателя
    private String refillAccountDetailsKs; // Кор счет банка-получателя
    private String refillAccountDetailsRs; // Транзитный счет банка-получателя для ОПИФ
    private String refillAccountDetailsRecipientName; // Наименование получателя платежа (Управляющая компания)
    private String refillAccountDetailsRecipientInn; // Инн получателя платежа (Управляющая компания)
    private String refillAccountDetailsPaymentPurpose; // Назначение платежа
    private String refillAccountDetailsMinSum; // Минимальная сумма перевода (для дополнительных перечислений в ОПИФ
}
