package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.AvailableReductionType;
import ru.bank.bsp.finance.model.Currency;

/**
 * Данные для старта оформления рассрочки
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExistingInstallmentDTO {
    private Integer operationQuantity; // Количество операций по рассрочке
    private Double monthlyPaymentAmount; // Величина следующего платежа по рассрочке
    private String nextPaymentDate; // Дата следующего платежа
    private Integer term; // Общий срок рассрочки
    private Integer paymentsLeft; // Осталось платежей
    private Double initialAmount; // Изначальная сумма рассрочки
    private Double totalSum; // Текущая общая сумма рассрочки
    private Double paidSum; // Сумма внесенных платежей по рассрочке
    private Double debtBalance; // Остаток задолженности
    private String iconUrl; // Ссылка на иконку
    private String fullTermsUrl; // Ссылка на иконку
    private String lastPaymentDate; // Дата последнего платежа
    private String installmentStartDate; // Дата открытия рассрочки
    private String installmentId; // Идентификатор рассрочки
    private AvailableReductionType availableReductionType; // Доступность полной/частичной отмены
    private Double reductionMinimumAmount; // Минимальная сумма для частичной отмены рассрочки
    private Double reductionMaximumAmount; // Максимальная сумма для частичной отмены рассрочки
    private Currency currency; // Валюта счета
    private OperationDTO operations; // Атрибуты операций
}
