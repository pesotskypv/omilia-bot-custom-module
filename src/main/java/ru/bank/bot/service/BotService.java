package ru.bank.bot.service;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import ru.bank.bot.DialogData;
import ru.bank.bot.model.Product;
import ru.bank.bot.utils.OutputMap;
import ru.bank.bsp.finance.model.CardProductType;
import ru.bank.bsp.finance.model.CreditProductType;
import ru.bank.bsp.finance.model.SavingsProductType;
import ru.bank.bsp.finance.model.Type;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;
import static ru.bank.bsp.card.service.CardService.findCards;
import static ru.bank.bsp.card.service.CardService.getCardNameForBot;
import static ru.bank.bsp.card.service.CardService.getCardPanLast4Dig;
import static ru.bank.bsp.card.service.CardService.setIcrByCard;
import static ru.bank.bsp.customerinfo.service.CustomerInfoService.getXauthUser;
import static ru.bank.bsp.customerinfo.service.CustomerInfoService.setIcrByCustomerInfo;
import static ru.bank.bsp.finance.service.FinanceService.findFinanceProduct;
import static ru.bank.bsp.finance.service.FinanceService.requestNewFinance;
import static ru.bank.bsp.finance.service.FinanceService.setIcrByFinance;
import static ru.bank.bsp.loyalty.service.LoyaltyService.findLoyaltyPrograms;
import static ru.bank.bsp.loyalty.service.LoyaltyService.setIcrByLoyalty;
import static ru.bank.util.Utils.dialogLog;
import static ru.bank.util.Utils.dialogLogWarn;
import static ru.bank.util.Utils.genericLog;
import static ru.bank.util.Utils.genericLogInfo;
import static ru.bank.util.Utils.genericLogWarn;

public class BotService {
    /**
     * Заполнение филдов ICR о наличии типов продуктов.
     */
    public static void getIcr(DialogData dialogData) {
        getXauthUser(dialogData);
        findCards(dialogData);
        requestNewFinance(null, dialogData);
        findLoyaltyPrograms(dialogData);
        setIcrByCustomerInfo(dialogData);
        setIcrByCard(dialogData);
        setIcrByFinance(dialogData);
        setIcrByLoyalty(dialogData);
    }

    /**
     * Получение атрибутов финансового продукта (счёт, кредит, карта) по фильтру.
     *
     * @param filter 16 или 4 цифры карты, точное имя продукта или фильтр (Card, CreditCard, currentAccount ...)
     */
    public static Map<String, ApiField> getBalance(String filter, DialogData dialogData) {
        OutputMap output = new OutputMap();
        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
        int counter = 1;

        findFinanceProduct(dialogData);
        if (dialogData.productsWithBalance == null || dialogData.productsWithBalance.size() == 0) {
            output.add("BEgetBalanceStatus", "error");
            return output.get();
        }

        List<String> names = dialogData.productsWithBalance.stream().map(Product::getName)
                .collect(Collectors.toList());
        Set<String> duplicates = names.stream().filter(n -> Collections.frequency(names, n) > 1)
                .collect(Collectors.toSet());
        if (!duplicates.isEmpty()) {
            dialogLogWarn(dialogData, "Одинаковое имя продуктов: " + duplicates);
            output.add("BEgetBalanceStatus", "sameNames");
            return output.get();
        }

        for (Product p : dialogData.productsWithBalance) {
            if (isMatchProduct(p, filter)) {
                output.add("BEproduct" + counter + "Name", p.getName());
                if (counter == 1) {
                    String amount = getAmountProduct(p);
                    Date creditNextPaymentDate = p.getCreditNextPaymentDate();
                    String creditNextPayment = p.getCreditNextPayment();
                    String contractNumber = p.getContractNumber();
                    Date gracePeriodPaymentDate = p.getGracePeriodPaymentDate();
                    String gracePeriodSum = getGracePeriodSumProduct(p);

                    if (amount != null) {
                        output.add("BEproduct" + counter + "Balance", amount);
                        output.add("BEproduct" + counter + "BalanceNegative", valueOf(isNegativeAmount(amount)));
                        output.add("BEproduct" + counter + "BalanceZero", valueOf(isZeroAmount(amount)));
                    }
                    if (gracePeriodPaymentDate != null) {
                        output.add("BEproduct" + counter + "GracePeriodPaymentDate",
                                formatter.format(gracePeriodPaymentDate));
                    }
                    if (gracePeriodSum != null) {
                        output.add("BEproduct" + counter + "GracePeriodPayment", gracePeriodSum);
                    }
                    if (creditNextPaymentDate != null) {
                        output.add("BEproduct" + counter + "NextPaymentDate",
                                formatter.format(creditNextPaymentDate));
                    }
                    if (creditNextPayment != null) {
                        output.add("BEproduct" + counter + "NextPayment", creditNextPayment
                                + " " + toCurrencySymbol(p.getCurrency()));
                    }
                    if (contractNumber != null) {
                        output.add("BEproduct" + counter + "ContractNumber", contractNumber);
                    }
                }
                counter++;
            }
        }

        // Если продукты есть, но под фильтр ни один не подпадает
        if (output.get().size() == 0 && dialogData.productsWithBalance.size() > 0) {
            StringBuilder products = new StringBuilder();
            for (Product p : dialogData.productsWithBalance) {
                products.append(p.getName()).append("; ");
            }
            dialogLog(dialogData, "Под фильтр " + filter + " не подпадает ни один из продуктов клиента: "
                    + products);
            output.add("BEgetBalanceStatus", "notFound");
        } else if (output.get().size() == 0) {
            output.add("BEgetBalanceStatus", "error");
        } else {
            output.add("BEgetBalanceStatus", "ok");
        }
        return output.get();
    }

    private static boolean isMatchProduct(Product product, String filter) {
        String name = product.getName();

        if (filter == null) {
            return false;
        }
        if (filter.equalsIgnoreCase("all") || product.getProductGroups().contains(filter)
                || name.equalsIgnoreCase(filter)
                || name.replaceAll("[Ёё]", "е").equalsIgnoreCase(filter)) {
            return true;
        }
        if (filter.matches("\\d{4}") && product.getNumber().endsWith(filter)) {
            return true;
        }
        if (filter.matches("\\d{16}") && isMatchMaskedNumber(product, filter)) {
            return true;
        }
        if (filter.matches("\\d{20}") && product.getAccountNumber().equals(filter)) {
            return true;
        }
        if (filter.contains(",")) {
            for (String filterOneOfMultiple : filter.split(",")) {
                if (product.getProductGroups().contains(filterOneOfMultiple)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Сравнение маскированного номера карты с немаскированным
     */
    private static boolean isMatchMaskedNumber(Product product, String number) {
        genericLogInfo("Выполняется matchMaskedNumber");
        String productNumber = product.getNumber();

        if (productNumber == null) {
            return false;
        } else if (number.length() != productNumber.length()) {
            return false;
        } else {
            for (int i = 0; i < number.length(); i++) {
                if (productNumber.charAt(i) == '*') {
                    genericLog("Если знак * то не проверяем равенство");
                } else if (number.charAt(i) == productNumber.charAt(i)) {
                    genericLog("... проверяем равенство цифр");
                } else {
                    genericLog("Если цифры не совпадают - возвращаем false");
                    return false;
                }
            }
            return true; // Если обошли все цифры и цикл не прервался - возвращаем true
        }
    }

    private static String getAmountProduct(Product product) {
        String amount = product.getAmount();
        String creditLimit = product.getCreditLimit();
        String currency = product.getCurrency();

        if (amount != null) {
            return amount + " " + toCurrencySymbol(currency);
        } else if (creditLimit != null) {
            return creditLimit + " " + toCurrencySymbol(currency);
        } else {
            return null;
        }
    }

    private static String getGracePeriodSumProduct(Product product) {
        String gracePeriodSum = product.getGracePeriodSum();
        String currency = product.getCurrency();

        if (gracePeriodSum != null) {
            return gracePeriodSum + " " + toCurrencySymbol(currency);
        } else {
            return null;
        }
    }

    private static boolean isNegativeAmount(String amount) {
        return amount.startsWith("-");
    }

    private static boolean isZeroAmount(String amount) {
        return amount.startsWith("0.00") || amount.startsWith("0.0") || amount.startsWith("0");
    }

    public static void setSavingParam(Product p) {
        String type = p.getType();
        Set<String> productGroups = p.getProductGroups();
        String currency = p.getCurrency();
        String accountNumber = p.getAccountNumber();

        if (type.equals(SavingsProductType.CURRENT.toString())) {
            p.setName("Текущий счёт " + currency + " *" + getCardPanLast4Dig(accountNumber));
            productGroups.add("currentAccount");
        }
        if (type.equals(SavingsProductType.ACCOUNT_SAVINGS.toString())) {
            p.setName("Накопительный счёт " + currency + " *" + getCardPanLast4Dig(accountNumber));
            productGroups.add("savingsAccount");
            productGroups.add("savingsOrDepositAccount");
        }
        if (type.equals(SavingsProductType.DEPOSIT.toString())) {
            p.setName("Вклад " + currency + " *" + getCardPanLast4Dig(accountNumber));
            productGroups.add("savingsOrDepositAccount");
        }
    }

    public static void setCreditParam(Product p) {
        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
        String type = p.getType();
        Set<String> productGroups = p.getProductGroups();
        String currency = p.getCurrency();
        String accountNumber = p.getAccountNumber();
        String creditContractStartDate = formatter.format(p.getCreditContractStartDate());

        if (type.equals(CreditProductType.ACCOUNT_CREDIT_CARD.toString()) ||
                type.equals(CreditProductType.ACCOUNT_NEW_CREDIT_CARD.toString())) {
            p.setName("Счёт кредитной карты *" + getCardPanLast4Dig(accountNumber));
            productGroups.add("creditCardAccount"); // credit не добавляем, чтобы не дублировалась карта и счёт
        }
        if (type.equals(CreditProductType.ACCOUNT_OVERDRAFT.toString())) {
            p.setName("Текущий счёт " + currency + " *" + getCardPanLast4Dig(accountNumber));
            productGroups.add("overdraft");
            productGroups.add("credit");
        }
        if (type.equals(CreditProductType.CREDIT_B.toString())) {
            p.setName("Потребительский кредит от " + creditContractStartDate);
            productGroups.add("potrebCredit");
            productGroups.add("credit");
        }
    }

    public static void setCardParam(Product p) {
        String type = p.getType();
        Set<String> productGroups = p.getProductGroups();

        p.setName(getCardNameForBot(p.getPaymentSystem(), p.getNumber()));
        productGroups.add("card");
        if (type.equals(CardProductType.CREDIT_CARD.toString())) {
            productGroups.add("creditCard");
            productGroups.add("credit");
        }
        if (type.equals(CardProductType.DEBIT_CARD.toString())) {
            productGroups.add("debitCard");
        }
    }

    public static void setMortgageParam(Product p) {
        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
        String type = p.getType();
        Set<String> productGroups = p.getProductGroups();
        String creditContractStartDate = formatter.format(p.getCreditContractStartDate());

        p.setName("Ипотека от " + creditContractStartDate);
        if (type.equals(Type.MORTGAGE.toString()) || type.equals(Type.CREDIT_DOM.toString()) ||
                type.equals(Type.CREDIT_LINE.toString()) || type.equals(Type.CREDIT_POS.toString())) {
            productGroups.add("mortgage");
            productGroups.add("credit");
        }
    }

    public static void setCarLoanParam(Product p) {
        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
        Set<String> productGroups = p.getProductGroups();
        String creditContractStartDate = formatter.format(p.getCreditContractStartDate());

        p.setName("Автокредит от " + creditContractStartDate);
        productGroups.add("autoCredit");
        productGroups.add("credit");
    }

    public static Map<String, ApiField> waitForSeconds(String seconds) {
        genericLogInfo("Выполняется waitForSeconds: " + seconds + " с");
        Map<String, ApiField> output = new HashMap<>();
        try {
            Thread.sleep(Long.parseLong(seconds) * 1000);
        } catch (InterruptedException e) {
            genericLogWarn("Ошибка при выполнении waitForSeconds: " + e);
            output.put("BEwaitForSecondsStatus", ApiField.builder().name("BEwaitForSecondsStatus")
                    .value("error").status(FieldStatus.DEFINED).build());
            return output;
        }
        output.put("BEwaitForSecondsStatus", ApiField.builder().name("BEwaitForSecondsStatus")
                .value("ok").status(FieldStatus.DEFINED).build());
        return output;
    }

    public static String toCurrencySymbol(String currency) {
        genericLogInfo("Выполняется toCurrencySymbol");
        String symbol = currency.toUpperCase();

        switch (symbol) {
            case "RUB":
            case "RUR":
                symbol = "₽";
                break;
            case "EUR":
                symbol = "€";
                break;
            case "USD":
                symbol = "$";
                break;
        }
        return symbol;
    }
}
