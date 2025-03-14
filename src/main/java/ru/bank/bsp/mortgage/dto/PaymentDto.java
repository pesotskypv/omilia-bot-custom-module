package ru.bank.bsp.mortgage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.mortgage.model.MortgageCurrency;
import ru.bank.bsp.mortgage.model.Type;

import java.util.List;

/**
 * Предстоящие платежи по кредиту
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Type type; // Тип платежа
    private Integer order; // Порядок отображения платежа в списке и иконки в виджете
    private Double amount; // Размер платежа
    private MortgageCurrency currency; // Валюта платежа
    private String date; // Дата платежа
    private List<ComponentDto> components; // Компоненты для графика детализации платежа
}
