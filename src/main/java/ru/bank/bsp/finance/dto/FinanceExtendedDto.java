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
public class FinanceExtendedDto {
    private List<SavingsExtendedDto> savings; // Массив текущих, сберегательных и депозитных счетов
    private List<CreditExtendedDto> credits; // Массив кредитов и счетов кредитных карт / овердрафтов
    private List<CardExtendedDto> cards;
    private List<InsuranceExtendedDto> insurances; // Массив страховок
    private List<BrokerageExtendedDto> brokerageAgreements;
    private List<InvestOpifExtendedDto> opifList; // Продукты Axiom ОПИФ
    private List<InvestIisDuExtendedDto> iisDuList; // Продукты Axiom ИИС ДУ
    private List<InvestDuExtendedDto> duList; // Продукты Axiom ДУ
    private List<InvestSduExtendedDto> sduList; // Продукты Axiom СДУ
    private Integer responseDelay; // Значение настройки сервиса responseDelay
    private String validityKey; // Токен валидности запроса, алгоритм в описании сервиса
}
