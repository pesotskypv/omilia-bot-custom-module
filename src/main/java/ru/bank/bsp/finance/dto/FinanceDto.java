package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Список финансовых продуктов
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinanceDto {
    private List<BalanceDto> balance; // Суммы для главной страницы
    private List<SavingsDto> savings; // Массив текущих, сберегательных и депозитных счетов
    private List<CreditDto> credits; // Массив кредитов и счетов кредитных карт / овердрафтов
    private List<CardDto> cards;
    private List<MortgageDto> mortgages; // Массив кредитов (ипотека)
    private List<CarloanDto> carloans; // Массив автокредитов с атрибутами до их появления в портфолио
    private List<InsuranceDto> insurances; // Атрибуты страховых и сервисных продуктов
    private List<BrokerageDto> brokerageAgreements;
    private List<InvestOpifDto> opifList; // Продукты Axiom ОПИФ
    private List<InvestIisDuDto> iisDuList; // Продукты Axiom ИИС ДУ
    private List<InvestDuDto> duList; // Продукты Axiom ДУ
    private List<InvestSduDto> sduList; // Продукты Axiom СДУ
    private List<ProductSettingsDto> settings; // Настройки продуктов в Evo
    private Integer responseDelay; // Значение настройки сервиса responseDelay
    private String validityKey; // токен валидности запроса, алгоритм описан в описании сервиса
}
