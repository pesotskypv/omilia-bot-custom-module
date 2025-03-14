package ru.bank.bsp.statement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Информация о доступных справках
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalAvailableStatementsDto {
    private Boolean mortgageEnable; // Наличие ипотечных кредитов у клиента
    private Boolean carloanEnable; // Наличие автокредитов у клиента
    private List<PersonalAvailableStatementCategoryDto> categories; // Список категорий каталогов
}
