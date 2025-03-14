package ru.bank.bsp.finance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ссылка на подробный тариф в pdf
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TariffInfoLink {
    private String iconUrl; // URL пиктограммы
    private String name; // Текст строки со ссылкой на подробные условия
    private String redirectUrl; // URL файла с подробными условиями
}
