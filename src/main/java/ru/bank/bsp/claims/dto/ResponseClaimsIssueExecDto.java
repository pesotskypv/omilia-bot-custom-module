package ru.bank.bsp.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.claims.model.ResponseMetaData;

/**
 * Ответ на PATCH /claims/v1/issue/{issueId}/executed
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseClaimsIssueExecDto {
    private ResponseMetaData result;
    private ClaimsIssueExecDto data;
}
