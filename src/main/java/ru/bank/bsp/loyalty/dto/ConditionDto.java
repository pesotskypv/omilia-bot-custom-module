package ru.bank.bsp.loyalty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Массив условий ПЛ
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionDto {
    private String conditionDescription; // Описание условия программы
    private String conditionIcon; // Ссылка на иконку для условия программы
    private String conditionName; // Название условия программы
    private Integer sortOrder; // Порядковый номер условия для отображения в МП
}
