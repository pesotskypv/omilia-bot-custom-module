package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Результат запроса получения деталей продукта
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailsObjectUidDto {
    private String productUid;
    private ProductDetailsItemDto details; // Детали продукта
}
