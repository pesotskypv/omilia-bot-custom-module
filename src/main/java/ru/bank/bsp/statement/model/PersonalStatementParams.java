package ru.bank.bsp.statement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Дополнительные параметры для создания справки
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalStatementParams {
    private String language; // Язык изготовления справки
    private List<PersonalStatementProductInfo> products; // Информация по банковским продуктам, по которым изготавливается справка
    private String createProductDate;
    private String rangeStartDate; // Дата начала периода example: 2022-10-24
    private String rangeEndDate; // Дата конца периода example: 2022-10-29
}
