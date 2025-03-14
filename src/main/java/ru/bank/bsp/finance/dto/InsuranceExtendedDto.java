package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.ProgramKind;

/**
 * Массив страховок
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceExtendedDto {
    private String productUid; // Идентификатор продукта
    private String productName; // Название продукта для внутренних каналов
    private ProgramKind programKind; // Тип продукта
    private String programDefinition; // Описание программы
    private String dealDate; // Дата оформления
    private String dealExpirationDate; // Дата окончания контракта
    private String dealNumber; // Номер договора
    private String dealStatus; // Статус продукта (код)
    private String dealStatusText; // Статус продукта (текст)
    private String programName; // Наименование программы страхования
    private String partnerName; // Наименование компании партнера
    private Double amount; // Стоимость программы
    private Boolean paymentState; // Статус оплаты
    private String term; // Срок страхования (в месяцах)
    private Boolean autoProlongationFlag; // Признак автопролонгации
    private String mainHint; // Подсказка к разделу продуктов на экране Мои продукты
    private String periodType; // Тип периода страхования
}
