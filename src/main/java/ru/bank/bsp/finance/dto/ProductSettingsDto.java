package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Настройки продуктов в Evo
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSettingsDto {
    private String productUid; // Уникальный идентификатор продукта
    private String productId; // Уникальный идентификатор продукта. Оставлен для обратной совместимости
    private Boolean showProductsScreen; // Отображение продукта на экране продуктов в Evo
    private Boolean showMainScreen; // Отображение продукта на главном экране в Evo
    private Integer sortOrderProductsScreen; // Порядок продукта на экране продуктов в Evo
    private Integer sortOrderMainScreen; // Порядок продукта на главном экране в Evo
}
