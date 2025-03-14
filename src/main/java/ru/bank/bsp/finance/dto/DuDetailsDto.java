package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.Currency;
import ru.bank.bsp.finance.model.State;

/**
 * Основная информация по продукту ДУ
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DuDetailsDto {
    private String contractNumber; // Номер договора
    private Currency currency; // Валюта договора
    private String strategyName; // Наименование стратегии
    private String dateSigning; // Дата подписания договора
    private String dateTermination; // Дата окончания договора
    private State state; // Cтатус договора
    private String provider; // Партнер
    private Double amount; // Сумма в валюте договора
    private Double equivalent; // Сумма договора в рублях
    private Double purchaseSumCurrency; // Сумма инвестирования в валюте договора
    private Double finResult; // Финансовый результат
    private Double yield; // Финансовый результат в процентном выражении
    private String dateCalculation; // Дата расчета стоимости активов
    private Boolean isStopOperations; // Признак приостановки операций по продукту
    private String dateStopOperations; // Дата обновления
}
