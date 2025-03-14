package ru.bank.bsp.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.card.model.ChangeStatus;


/**
 * Изменить статус карты
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCardStatusRequest {
    private ChangeStatus status;
}
