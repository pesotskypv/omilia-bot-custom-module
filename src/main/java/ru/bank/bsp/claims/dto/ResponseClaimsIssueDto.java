package ru.bank.bsp.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.claims.model.ResponseMetaData;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseClaimsIssueDto {
    private ResponseMetaData result;
    private ClaimsIssueDto data;
}
