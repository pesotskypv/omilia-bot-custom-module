package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.ResponseMetaData;

/**
 * Детальная информация по продукту
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailsByUidResponse {
    private ResponseMetaData result;
    private ProductDetailsObjectUidDto data;
}
