package ru.bank.bsp.loyalty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.loyalty.model.ResponseMetaData;

import java.util.List;

/**
 * Информация подключенных ПЛ пользователя
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseListLoyaltyProgramDto {
    private List<LoyaltyProgramDto> data; // ПЛ доступные пользователю и карты, которые подключены к ПЛ
    private ResponseMetaData result;
}
