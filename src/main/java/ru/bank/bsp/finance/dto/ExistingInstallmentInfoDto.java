package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Данные о наличии текущих рассрочек
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExistingInstallmentInfoDto {
    private String iconUrl; // URL пиктограммы
    private String name; // Текст строки c текущими рассрочками
}
