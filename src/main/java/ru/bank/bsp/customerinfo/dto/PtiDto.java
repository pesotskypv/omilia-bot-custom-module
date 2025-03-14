package ru.bank.bsp.customerinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.customerinfo.model.Source;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PtiDto {
    private Double value; // Показатель долговой нагрузки (ПДН)
    private String date; // Дата расчета ПДН
    private Source source; // Система источник значения ПДН
}
