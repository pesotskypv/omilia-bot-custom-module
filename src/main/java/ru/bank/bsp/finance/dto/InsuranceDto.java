package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.ProgramKind;

/**
 * Атрибуты страховых и сервисных продуктов
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceDto {
    private String productUid; // id продукта
    private String productId; // id продукта по dboId. Оставлен для обратной совместимости, совпадает с productUid
    private String productName; // Название продукта для внешних каналов. Отличается от названия для внутренних каналов
    private String programDefinition; // Описание программы
    private String dealExpirationDate; // Дата окончания контракта
    private ProgramKind programKind; // Вид продукта
    private String dealStatus; // Статус продукта (код)
    private String dealStatusText; // Статус продукта (текст)
    private String programName; // Наименование программы страхования
    private String partnerName; // Наименование компании партнера
    private String dealNumber; // Номер договора
    private Double amount; // Стоимость программы
    private Boolean paymentState; // Статус оплаты
    private String dealDate; // Дата оформления
    private String term; // Срок страхования (в месяцах)
    private Boolean autoProlongationFlag; // Признак автопролонгации
    private String mainHint; // Подсказка к разделу продуктов на экране Мои продукты
    private String periodType; // Тип периода страхования
}
