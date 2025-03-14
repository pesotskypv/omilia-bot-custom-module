package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Реквизиты для пополнения счета
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestRequisitesDto {
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
