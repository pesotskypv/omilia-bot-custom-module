package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.Style;

/**
 * Детали продукта
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailsItemDto {
    private String iconUrl; // URL пиктограммы
    private Integer order; // Порядок отображения строки по возрастанию
    private String id; // Краткий текстовый идентификатор поля данных
    private String parentId; // Идентификатор поля для которого данное является составным элементом
    private String name; // Заголовок поля данных
    private String value; // Значение поля данных
    private String hint; // Подсказка
    private Style style; // Стиль поля данных
}
