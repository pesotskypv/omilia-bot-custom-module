package ru.bank.bsp.statement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.statement.model.Currency;
import ru.bank.bsp.statement.model.ProductType;

/**
 * Продукты, по которым можно сформировать справку
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoDto {
    private String id; // Идентификатор продукта
    private ProductType type; // Тип продукта
    private String code; // Код продукта (только для LOAN)
    private String name; // Название продукта
    private Double amount; // Сумма на счете
    private Currency currency; // Валюта продукта

    public String getClearedName() {
        return name.replaceAll("#", "")
                .replaceAll("ё", "е").replaceAll("Ё", "Е");
    }
}
