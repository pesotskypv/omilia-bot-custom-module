package ru.bank.bsp.customerinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseDto {
    private String clientSegmentGroup; // Сегмент клиента
    private List<BisIdDtoFeign> customerIds; // Список bis идентификаторов клиента
    private PtiDto pti;
    private String dboId; // Идентификатор клиента
    private String email; // email клиента
    private String firstName; // Имя клиента
    private String lastName; // Фамилия клиента
    private String middleName; // Отчество клиента
    private String phone; // Сотовый номер клиента
    private String siebelId; // CRM Идентификатор клиента
    private String xauthUser;
}
