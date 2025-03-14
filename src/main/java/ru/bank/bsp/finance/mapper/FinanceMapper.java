package ru.bank.bsp.finance.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bank.bot.model.Product;
import ru.bank.bsp.finance.dto.CardDto;
import ru.bank.bsp.finance.dto.CarloanDto;
import ru.bank.bsp.finance.dto.CreditDto;
import ru.bank.bsp.finance.dto.CreditExtendedDto;
import ru.bank.bsp.finance.dto.MortgageDto;
import ru.bank.bsp.finance.dto.SavingsDto;
import ru.bank.bsp.finance.dto.SavingsExtendedDto;

@Mapper
public interface FinanceMapper {
    @Mapping(target = "dboId", source = "savingsDto.productId")
    @Mapping(target = "accountNumber", source = "savingsExtendedDto.accountNumber")
    @Mapping(target = "amount", source = "savingsDto.amount")
    @Mapping(target = "name", source = "savingsDto.name")
    @Mapping(target = "currency", source = "savingsDto.currency")
    @Mapping(target = "type", source = "savingsDto.productType")
    Product savingDtosToProduct(SavingsDto savingsDto, SavingsExtendedDto savingsExtendedDto);

    @Mapping(target = "dboId", source = "creditDto.productId")
    @Mapping(target = "accountNumber", source = "creditExtendedDto.accountNumber")
    @Mapping(target = "amount", source = "creditDto.amount")
    @Mapping(target = "name", source = "creditDto.name")
    @Mapping(target = "currency", source = "creditDto.currency")
    @Mapping(target = "type", source = "creditDto.productType")
    @Mapping(target = "creditLimit", source = "creditDto.accountAmount")
    @Mapping(target = "gracePeriodSum", source = "creditDto.gracePeriod.gracePeriodAmount")
    @Mapping(target = "gracePeriodPaymentDate", source = "creditDto.gracePeriod.gracePeriodEndDate",
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Mapping(target = "creditContractStartDate", source = "creditDto.openDate",
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Mapping(target = "contractNumber", source = "creditDto.contractNumber")
    @Mapping(target = "creditNextPayment", source = "creditDto.schedulePayment.fullAmount")
    @Mapping(target = "creditNextPaymentDate", source = "creditDto.schedulePayment.paymentDate",
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    Product creditDtosToProduct(CreditDto creditDto, CreditExtendedDto creditExtendedDto);

    @Mapping(target = "dboId", source = "cardDto.productId")
    @Mapping(target = "name", source = "cardDto.name")
    @Mapping(target = "type", source = "cardDto.productType")
    @Mapping(target = "paymentSystem", source = "cardDto.paymentSystem")
    @Mapping(target = "number", source = "cardDto.cardNumber")
    @Mapping(target = "amount", source = "savingsDto.amount")
    @Mapping(target = "currency", source = "savingsDto.currency")
    Product debitCardDtoToProduct(CardDto cardDto, SavingsDto savingsDto);

    @Mapping(target = "dboId", source = "cardDto.productId")
    @Mapping(target = "name", source = "cardDto.name")
    @Mapping(target = "type", source = "cardDto.productType")
    @Mapping(target = "paymentSystem", source = "cardDto.paymentSystem")
    @Mapping(target = "number", source = "cardDto.cardNumber")
    @Mapping(target = "amount", source = "creditDto.amount")
    @Mapping(target = "creditLimit", source = "creditDto.accountAmount")
    @Mapping(target = "gracePeriodSum", source = "creditDto.gracePeriod.gracePeriodAmount")
    @Mapping(target = "gracePeriodPaymentDate", source = "creditDto.gracePeriod.gracePeriodEndDate",
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Mapping(target = "currency", source = "creditDto.currency")
    @Mapping(target = "creditNextPayment", source = "creditDto.schedulePayment.fullAmount")
    @Mapping(target = "creditNextPaymentDate", source = "creditDto.schedulePayment.paymentDate",
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    Product creditCardDtoToProduct(CardDto cardDto, CreditDto creditDto);

    @Mapping(target = "dboId", source = "mortgageDto.productId")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "name", source = "mortgageDto.name")
    @Mapping(target = "currency", source = "mortgageDto.currency")
    @Mapping(target = "type", source = "mortgageDto.type")
    @Mapping(target = "creditContractStartDate", source = "mortgageDto.contractStartDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "contractNumber", source = "mortgageDto.contractNumber")
    @Mapping(target = "creditNextPayment", source = "mortgageDto.schedulePayment.fullAmount")
    @Mapping(target = "creditNextPaymentDate", source = "mortgageDto.schedulePayment.paymentDate",
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    Product mortgageDtoToProduct(MortgageDto mortgageDto, Double amount);

    @Mapping(target = "dboId", source = "carloanDto.productId")
    @Mapping(target = "amount", source = "carloanDto.amount")
    @Mapping(target = "contractNumber", source = "carloanDto.contractNumber")
    @Mapping(target = "creditContractStartDate", source = "carloanDto.openDate",
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Mapping(target = "name", source = "carloanDto.name")
    @Mapping(target = "currency", source = "carloanDto.currency")
    @Mapping(target = "type", source = "carloanDto.productType")
    @Mapping(target = "creditNextPayment", source = "carloanDto.schedulePayment.fullAmount")
    @Mapping(target = "creditNextPaymentDate", source = "carloanDto.schedulePayment.paymentDate",
            dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Mapping(target = "accountNumber", source = "savingsExtendedDto.accountNumber")
    Product carloanDtoToProduct(CarloanDto carloanDto, SavingsExtendedDto savingsExtendedDto);
}
