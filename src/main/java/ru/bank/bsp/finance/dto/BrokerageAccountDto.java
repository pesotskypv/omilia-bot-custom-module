package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.Currency;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrokerageAccountDto {
    private String fullNameClient; // ФИО владельца счета
    private String accountNumber; // Номер счета
    private Currency accountCurrency; // Валюта счета
    private String bank; // Наименование банка, в котором выпущен счет
    private String bik; // БИК банка, в котором выпущен счет
    private String bankInn; // ИНН банка, в котором выпущен счет
    private String corrAccount; // Корр. счет банка, в котором выпущен счет
}
