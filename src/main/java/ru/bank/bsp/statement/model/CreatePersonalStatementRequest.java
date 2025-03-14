package ru.bank.bsp.statement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Создание заявки на формирование справки
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePersonalStatementRequest {
    private String initSystem; // Система, отправившая запрос
    private String extension; // Расширение файла
    private StatementType type; // Тип справки
    private PersonalStatementParams params; // Дополнительные параметры для создания справки
}
