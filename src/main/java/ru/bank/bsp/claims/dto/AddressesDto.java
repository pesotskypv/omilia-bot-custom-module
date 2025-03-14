package ru.bank.bsp.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Данные об адресе клиента
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressesDto {
    private String name; // Адрес регистрации
    private String value; // Адрес клиента
}
