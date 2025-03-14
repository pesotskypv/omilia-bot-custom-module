package ru.bank.bsp.tariff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.tariff.model.TariffStatus;

import java.util.List;

/**
 * Тариф пользователя
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TariffDto {
    private String packageCode; // Код ПБУ
    private String tariffName; // Название тарифа
    private String branchName; // Название филиала, в котором открыт ПБУ
    private TariffStatus status; // Статус тарифа
    private List<GroupDto> group; // Группы комиссий
}
