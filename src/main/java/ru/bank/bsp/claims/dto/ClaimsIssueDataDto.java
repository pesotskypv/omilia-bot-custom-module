package ru.bank.bsp.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Данные для создания заявки
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimsIssueDataDto {
    private String legalEntitySiebelId; // Идентификатор юридического лица
    private String operationType; // Тип операции
    private String operationRequestId; // Идентификатор запроса на проведение операции
    private String requestCreateDate; // Дата и время создания заявки
}
