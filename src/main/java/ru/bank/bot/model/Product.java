package ru.bank.bot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Продукт, у которого есть баланс: счёт или карта.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String dboId;
    private String name;
    private String amount;
    private String currency;
    private String number;
    private String creditLimit;
    private String creditNextPayment;
    private Date creditNextPaymentDate;
    private String gracePeriodSum;
    private Date gracePeriodPaymentDate;
    private String type;
    private String contractNumber;
    private String accountNumber;
    private Date creditContractStartDate;
    private String paymentSystem;
    private final Set<String> productGroups = new HashSet<>();
}
