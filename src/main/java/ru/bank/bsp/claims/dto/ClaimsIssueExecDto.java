package ru.bank.bsp.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Результат запроса на выполнение заявки
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimsIssueExecDto {
    private String issueId; // Идентификатор заявки
    private String state; // Статус заявки
    private String comment; // Комментарий к способу доставки
}
