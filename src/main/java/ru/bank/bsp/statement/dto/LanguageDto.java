package ru.bank.bsp.statement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Доступный язык для изготовления справки
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageDto {
    private String name; // Код
    private String text; // Название
}
