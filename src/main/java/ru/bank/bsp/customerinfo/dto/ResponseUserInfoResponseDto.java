package ru.bank.bsp.customerinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.customerinfo.model.ResponseMetaData;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserInfoResponseDto {
    private UserInfoResponseDto data;
    private ResponseMetaData result;
}
