package ru.bank.bsp.mortgage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.mortgage.model.Value;

/**
 * Компоненты для графика детализации платежа
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentDto {
    private String name; // Имя компонент
    private Value value; // Величина компонента
    private Integer order; // Порядок отображения компонента
}
