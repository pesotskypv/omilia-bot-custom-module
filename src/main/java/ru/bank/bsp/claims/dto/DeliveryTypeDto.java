package ru.bank.bsp.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Способ получения ответа на обращение
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryTypeDto {
    private String code; // Код способа доставки
    private String name; // Название способа доставки
    private String hint; // Подсказка к способу досставки
    private String sort; // Порядок сортировки
    private List<AddressesDto> addresses; // Список адресов клиента
}
