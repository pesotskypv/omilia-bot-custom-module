package ru.bank.bsp.statement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Адрес клиента
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullClientAddressDto {
    private String fullClientAddress; // Адрес клиента
}
