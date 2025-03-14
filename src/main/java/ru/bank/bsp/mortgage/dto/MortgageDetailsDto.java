package ru.bank.bsp.mortgage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.mortgage.model.MortgageCurrency;

import java.util.List;

/**
 * Детальная информация по кредиту
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MortgageDetailsDto {
    private String imageUrl; // Ссылка на изображение - домик для экрана ипотеки
    private MortgageCurrency currency; // Валюта кредита
    private Double remainderLine; // Доступный остаток кредитной линии
    private Boolean directDebitState; // Флаг - подключено автосписани
    private Double paymentAmount; // Сумма предстоящих платежей
    private String paymentCurrency; // Валюта суммы предстоящих платежей. Может отличаться от валюты кредита: например, для валютной ипотеки на баннере могут быть судебные издержки в рублях
    private String paymentDate; // Дата следующего платежа: для разведения досрочного и регулярного платежа на баннере
    private List<PaymentDto> payments; // Предстоящие платежи по кредиту
    private AdditionalAccountDto additionalAccount; // Атрибуты счёта обслуживания кредита
    private AdditionalAccountDto factoringAccount; // Атрибуты счёта обслуживания кредита
}
