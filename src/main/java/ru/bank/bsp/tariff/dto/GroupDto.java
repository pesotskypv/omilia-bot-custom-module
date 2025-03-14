package ru.bank.bsp.tariff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.tariff.model.Name;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {
    private Name name; // Название группы комиссий
    private List<CommissionParametersDto> operations;
}
