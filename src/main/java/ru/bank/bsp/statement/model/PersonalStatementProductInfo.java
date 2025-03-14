package ru.bank.bsp.statement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация по продукту, для которого изготавливается справка
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalStatementProductInfo {
    private String id; // Идентификатор продукта
    private ProductType type; // Тип продукта
}
