package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.Currency;

/**
 * Суммы для главной страницы
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDto {
    private Double totalFunds; //Сумма средств всех счетов сконвертированная в одну из валют. Формируется из ответа portfolio-app и currencyrate-atomic.
    private Double totalAmount; // Сумма средств на счетах в одной из валют. Формируется из ответа portfolio-app.
    private Currency currency; // Валюта
    private Double ownAmount; // Сумма свободных средств в одной из валют. Формируется из ответа portfolio-app.
    private Double creditLine; // Сумма доступных кредитных и овердрафтных лимитов в одной из валют. Формируется из ответа portfolio-app.
    private Double savingsAmount; // Сумма средств на депозитах и сберсчетах в одной из валют. Формируется из ответа portfolio-app.
    private Double currentExpenses; // Сумма предстоящих расходов на месяц в одной из валют. Формируется из ответов portfolio-app, mortgage-app, carloan-app (при наличии ошибок или таймаутов - только из успешных ответов)
}
