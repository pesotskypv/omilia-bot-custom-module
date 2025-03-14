package ru.bank.bsp.finance.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import com.omilia.diamant.dialog.components.fields.ApiField;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.net.URIBuilder;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.bot.model.Product;
import ru.bank.bot.utils.OutputMap;
import ru.bank.client.dto.BotHttpResponseDto;
import ru.bank.bsp.card.dto.GetCardsResponse;
import ru.bank.bsp.customerinfo.service.CustomerInfoService;
import ru.bank.bsp.finance.dto.*;
import ru.bank.bsp.finance.mapper.FinanceMapper;
import ru.bank.bsp.finance.mapper.FinanceMapperImpl;
import ru.bank.bsp.finance.model.*;
import ru.bank.bsp.mortgage.dto.AdditionalAccountDto;
import ru.bank.bsp.mortgage.dto.MortgageDetailsResult;

import java.net.URISyntaxException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static ru.bank.bot.service.BotService.setCarLoanParam;
import static ru.bank.bot.service.BotService.setCardParam;
import static ru.bank.bot.service.BotService.setCreditParam;
import static ru.bank.bot.service.BotService.setMortgageParam;
import static ru.bank.bot.service.BotService.setSavingParam;
import static ru.bank.bot.service.BotService.toCurrencySymbol;
import static ru.bank.client.BotHttpClient.invokeGet;
import static ru.bank.bsp.card.service.CardService.findCards;
import static ru.bank.bsp.card.service.CardService.getCardNameForBot;
import static ru.bank.bsp.card.service.CardService.getCardPanLast4Dig;
import static ru.bank.bsp.mortgage.service.MortgageService.getMortgageByProductUid;
import static ru.bank.util.Utils.dialogLog;
import static ru.bank.util.Utils.dialogLogInfo;
import static ru.bank.util.Utils.dialogLogWarn;
import static ru.bank.util.Utils.genericLog;
import static ru.bank.util.Utils.genericLogInfo;
import static ru.bank.util.Utils.genericLogWarn;

/**
 * Finance-API сервис для получения информации о списке финансовых продуктов пользователя.
 */
public class FinanceService {
    private static final FinanceMapper financeMapper = new FinanceMapperImpl();

    /**
     * Получение списка продуктов клиента
     */
    public static void findFinanceProduct(DialogData dialogData) {
        genericLogInfo("Выполняется findFinanceProduct");
        dialogData.productsWithBalance = new HashSet<>();

        findFinance(dialogData, FinanceListType.NewFinance);
        findFinance(dialogData, FinanceListType.FinanceExtended);
        findCards(dialogData);

        if (dialogData.finance != null && dialogData.financeExtended != null
                && !dialogData.finance.getData().getSavings().isEmpty()
                && !dialogData.financeExtended.getData().getSavings().isEmpty()) {
            dialogData.finance.getData().getSavings().forEach(s -> {
                String productUid = s.getProductUid();
                if (productUid != null) {
                    SavingsExtendedDto savingsExtendedDto = dialogData.financeExtended.getData().getSavings().stream()
                            .filter(se -> se.getProductUid().equals(productUid)).findFirst().orElse(null);
                    if (savingsExtendedDto != null) {
                        Product product = financeMapper.savingDtosToProduct(s, savingsExtendedDto);
                        setSavingParam(product);
                        dialogData.productsWithBalance.add(product);
                    }
                }
            });
        } else {
            dialogLog(dialogData, "У клиента нет сберегателтных счетов.");
        }

        if (dialogData.finance != null && dialogData.financeExtended != null
                && !dialogData.finance.getData().getCredits().isEmpty()
                && !dialogData.financeExtended.getData().getCredits().isEmpty()) {
            dialogData.finance.getData().getCredits().stream()
                    .filter(c -> !c.getState().equals(State.CLOSED)).forEach(c -> {
                        String productUid = c.getProductUid();
                        if (productUid != null) {
                            CreditExtendedDto creditExtendedDto = dialogData.financeExtended.getData().getCredits()
                                    .stream().filter(ce -> ce.getProductUid().equals(productUid)).findFirst()
                                    .orElse(null);
                            if (creditExtendedDto != null) {
                                Product product = financeMapper.creditDtosToProduct(c, creditExtendedDto);
                                setCreditParam(product);
                                dialogData.productsWithBalance.add(product);
                            }
                        }
                    });
        } else {
            dialogLog(dialogData, "У клиента нет кредитных счетов.");
        }

        if (dialogData.finance != null && !dialogData.finance.getData().getCards().isEmpty()
                && (!dialogData.finance.getData().getSavings().isEmpty()
                || !dialogData.finance.getData().getCredits().isEmpty())) {
            List<String> cardIds = dialogData.cards.getData().stream().map(GetCardsResponse::getId)
                    .collect(Collectors.toList());
            dialogData.finance.getData().getCards().stream()
                    .filter(c -> cardIds.contains(c.getProductId())).forEach(c -> {
                        String primaryAccountUid = c.getPrimaryAccountUid();
                        CardProductType type = c.getProductType();
                        Product product = null;

                        if (primaryAccountUid != null && type != null && type.equals(CardProductType.DEBIT_CARD)) {
                            SavingsDto savingsDto = dialogData.finance.getData().getSavings().stream()
                                    .filter(s -> s.getProductUid().equals(primaryAccountUid)).findFirst()
                                    .orElse(null);
                            if (savingsDto != null) {
                                product = financeMapper.debitCardDtoToProduct(c, savingsDto);
                            }
                            if (product == null) {
                                CreditDto creditDto = dialogData.finance.getData().getCredits().stream()
                                        .filter(cr -> cr.getProductUid().equals(primaryAccountUid)).findFirst()
                                        .orElse(null);
                                if (creditDto != null) {
                                    product = financeMapper.creditCardDtoToProduct(c, creditDto);
                                }
                            }
                        } else if (primaryAccountUid != null && type != null
                                && type.equals(CardProductType.CREDIT_CARD)) {
                            CreditDto creditDto = dialogData.finance.getData().getCredits().stream()
                                    .filter(cr -> cr.getProductUid().equals(primaryAccountUid)).findFirst()
                                    .orElse(null);
                            if (creditDto != null) {
                                product = financeMapper.creditCardDtoToProduct(c, creditDto);
                            }
                        }
                        if (product != null) {
                            setCardParam(product);
                            dialogData.productsWithBalance.add(product);
                        }
                    });
        }

        if (dialogData.finance != null && !dialogData.finance.getData().getMortgages().isEmpty()) {
            dialogData.finance.getData().getMortgages().stream().filter(m -> !m.getState().equals(State.CLOSED))
                    .forEach(m -> {
                        String productUid = m.getProductUid();
                        Double amount = null;
                        if (productUid != null) {
                            MortgageDetailsResult mortgage = getMortgageByProductUid(m.getProductUid(), dialogData);
                            if (mortgage != null) {
                                AdditionalAccountDto additionalAccount = mortgage.getData().getAdditionalAccount();
                                if (additionalAccount != null) {
                                    amount = mortgage.getData().getAdditionalAccount().getAmount();
                                }
                            }
                        }
                        if (amount != null) {
                            Product product = financeMapper.mortgageDtoToProduct(m, amount);
                            setMortgageParam(product);
                            dialogData.productsWithBalance.add(product);
                        }
                    });
        }

        if (dialogData.finance != null && dialogData.financeExtended != null
                && !dialogData.finance.getData().getSavings().isEmpty() &&
                !dialogData.financeExtended.getData().getSavings().isEmpty() &&
                !dialogData.finance.getData().getCarloans().isEmpty()) {
            dialogData.finance.getData().getCarloans().stream()
                    .filter(c -> !c.getState().equals(State.CLOSED)).forEach(c -> {
                        String productId = c.getRepaymentAccountId();
                        if (productId != null) {
                            String productUid = dialogData.finance.getData()
                                    .getSavings().stream().filter(s -> s.getProductId().equals(productId)).findFirst()
                                    .map(SavingsDto::getProductUid).map(Object::toString).orElse(null);
                            if (productUid != null) {
                                SavingsExtendedDto savingsExtendedDto = dialogData.financeExtended.getData()
                                        .getSavings().stream().filter(se -> se.getProductUid().equals(productUid))
                                        .findFirst().orElse(null);
                                if (savingsExtendedDto != null) {
                                    Product product = financeMapper.carloanDtoToProduct(c, savingsExtendedDto);
                                    setCarLoanParam(product);
                                    dialogData.productsWithBalance.add(product);
                                }
                            }
                        }
                    });
        }
        if (!dialogData.productsWithBalance.isEmpty()) {
            dialogLog(dialogData, "Список продуктов клиента: " + dialogData.productsWithBalance);
        }
    }

    /**
     * BackEndCall BEgetCardOptionsStatus Получение списка опций по карте
     */
    public static Map<String, ApiField> getCardOptions(String cardName, DialogData dialogData) {
        genericLogInfo("Выполняется getCardOptions");
        OutputMap output = new OutputMap();
        final String cardPan;

        if (cardName != null && cardName.matches(".*\\d{4}$")) {
            cardPan = getCardPanLast4Dig(cardName);
        } else {
            dialogLogWarn(dialogData, "Некорректно указаны последние 4 цифры номера платёжной карты: "
                    + cardName);
            output.add("BEgetCardOptionsStatus", "badRequest");
            return output.get();
        }
        if (dialogData.cards == null) {
            dialogLogWarn(dialogData, "Отсутствует список карт клиента");
            output.add("BEgetCardOptionsStatus", "error");
            return output.get();
        }

        String cardId = dialogData.cards.getData().stream()
                .filter(c -> cardPan.equals(getCardPanLast4Dig(c.getCardNumber()))).findFirst()
                .map(GetCardsResponse::getId).map(Object::toString).orElse(null);
        if (cardId == null) {
            dialogLogWarn(dialogData, "Отсутствует ИД карты.");
            output.add("BEgetCardOptionsStatus", "badRequest");
            return output.get();
        }

        findFinance(dialogData, FinanceListType.NewFinance);
        if (dialogData.finance == null) {
            dialogLogWarn(dialogData, "Отсутствует список финансовых продуктов клиента.");
            output.add("BEgetCardOptionsStatus", "error");
            return output.get();
        }

        String cardUid = dialogData.finance.getData().getCards().stream().filter(c -> c.getProductId().equals(cardId))
                .findFirst().map(CardDto::getProductUid).map(Object::toString).orElse(null);
        if (cardUid == null) {
            dialogLogWarn(dialogData, "Отсутствует Uid карты.");
            output.add("BEgetCardOptionsStatus", "error");
            return output.get();
        }

        findFinance(dialogData, FinanceListType.FinanceExtended);
        if (dialogData.financeExtended == null) {
            dialogLogWarn(dialogData, "Отсутствует расширенный список финансовых продуктов клиента.");
            output.add("BEgetCardOptionsStatus", "error");
            return output.get();
        }

        CardExtendedDto cardExt = dialogData.financeExtended.getData().getCards().stream()
                .filter(c -> c.getProductUid().equals(cardUid))
                .findFirst().orElse(null);
        if (cardExt != null) {
            PinGenerationMethod pin = cardExt.getPinGenerationMethod();

            if (pin != null) {
                output.add("BEcardPinGenerationMethod", pin.toString());
            }
        } else {
            dialogLogWarn(dialogData, "Отсутствует карта с productUid: " + cardUid);
            output.add("BEgetCardOptionsStatus", "error");
            return output.get();
        }
        if (output.get().size() > 0) {
            output.add("BEgetCardOptionsStatus", "ok");
        } else {
            output.add("BEgetCardOptionsStatus", "notFound");
        }
        return output.get();
    }

    /**
     * BackEndCall BEgetCardFinanceStatus Получение финансовой информации по карте
     */
    public static Map<String, ApiField> getCardFinance(String cardName, DialogData dialogData) {
        genericLogInfo("Выполняется getCardFinance");
        OutputMap output = new OutputMap();
        final String cardPan;
        String primaryAccountUid;
        CardProductType type;
        String paymentSystem;
        String cardNumber;

        if (cardName != null && cardName.matches(".*\\d{4}$")) {
            cardPan = getCardPanLast4Dig(cardName);
        } else {
            dialogLogWarn(dialogData, "Некорректно указаны последние 4 цифры номера платёжной карты: "
                    + cardName);
            output.add("BEgetCardFinanceStatus", "badRequest");
            return output.get();
        }
        if (dialogData.cards == null) {
            dialogLogWarn(dialogData, "Отсутствует список карт клиента");
            output.add("BEgetCardFinanceStatus", "error");
            return output.get();
        }

        String cardId = dialogData.cards.getData().stream()
                .filter(c -> cardPan.equals(getCardPanLast4Dig(c.getCardNumber()))).findFirst()
                .map(GetCardsResponse::getId).map(Object::toString).orElse(null);
        if (cardId == null) {
            dialogLogWarn(dialogData, "Отсутствует ИД карты.");
            output.add("BEgetCardFinanceStatus", "badRequest");
            return output.get();
        }

        findFinance(dialogData, FinanceListType.NewFinance);
        if (dialogData.finance == null) {
            dialogLogWarn(dialogData, "Отсутствует список финансовых продуктов клиента");
            output.add("BEgetCardFinanceStatus", "error");
            return output.get();
        }

        CardDto card = dialogData.finance.getData().getCards().stream().filter(c -> c.getProductId().equals(cardId))
                .findFirst().orElse(null);
        if (card != null) {
            primaryAccountUid = card.getPrimaryAccountUid();
            type = card.getProductType();
            paymentSystem = card.getPaymentSystem();
            cardNumber = card.getCardNumber();
        } else {
            dialogLogWarn(dialogData, "Отсутствует список карт клиента");
            output.add("BEgetCardFinanceStatus", "error");
            return output.get();
        }
        if (primaryAccountUid == null || type == null) {
            dialogLogWarn(dialogData, "Отсутствует Uid или тип карты.");
            output.add("BEgetCardFinanceStatus", "error");
            return output.get();
        }
        if (paymentSystem != null && cardNumber != null) {
            output.add("BEcard", getCardNameForBot(paymentSystem, cardNumber));
        } else {
            dialogLogWarn(dialogData, "Отсутствует платёжня система или номер карты.");
            output.add("BEgetCardFinanceStatus", "error");
            return output.get();
        }
        if (type.equals(CardProductType.DEBIT_CARD)) {
            SavingsDto savingsDto = dialogData.finance.getData().getSavings().stream()
                    .filter(s -> s.getProductUid().equals(primaryAccountUid)).findFirst().orElse(null);
            if (savingsDto != null) {
                Double amount = savingsDto.getAmount();
                Currency currency = savingsDto.getCurrency();

                if (amount != null && currency != null) {
                    output.add("BEcardAmount", amount + " " + toCurrencySymbol(currency.toString()));
                    if (amount < 0) {
                        output.add("BEcardAmountNegative", "true");
                    } else if (amount == 0) {
                        output.add("BEcardAmountZero", "true");
                    }
                } else {
                    dialogLogWarn(dialogData, "Отсутствует остаток на счету или валюта счёта.");
                    output.add("BEgetCardFinanceStatus", "error");
                    return output.get();
                }
            } else {
                if (findCreditsInfo(primaryAccountUid, output, dialogData)) {
                    return output.get();
                }
            }
        } else if (type.equals(CardProductType.CREDIT_CARD)) {
            if (findCreditsInfo(primaryAccountUid, output, dialogData)) {
                return output.get();
            }
        }
        if (output.get().size() > 0) {
            output.add("BEgetCardFinanceStatus", "ok");
        } else {
            output.add("BEgetCardFinanceStatus", "notFound");
        }
        return output.get();
    }

    /**
     * Получение списка КК
     */
    public static Map<String, ApiField> findCardsCreditFinance(DialogData dialogData) {
        genericLogInfo("findCardsCreditFinance Для анализа");
        dialogLogInfo(dialogData, "findCardsCreditFinance Для анализа");
        OutputMap output = new OutputMap();
        AtomicReference<String> primaryAccountUid = new AtomicReference<>();
        AtomicReference<String> paymentSystem = new AtomicReference<>();
        AtomicReference<String> cardNumber = new AtomicReference<>();

        findFinance(dialogData, FinanceListType.NewFinance);
        if (dialogData.finance == null) {
            dialogLogWarn(dialogData, "Отсутствует список финансовых продуктов клиента");
            output.add("beFindCardsCreditFinanceStatus", "error");
            return output.get();
        }

        dialogData.finance.getData().getCards().stream()
                .filter(c -> c.getProductType().equals(CardProductType.CREDIT_CARD))
                .forEach(c -> {
                    primaryAccountUid.set(c.getPrimaryAccountUid());
                    paymentSystem.set(c.getPaymentSystem());
                    cardNumber.set(c.getCardNumber());
                    CreditDto creditDto = dialogData.finance.getData().getCredits().stream()
                            .filter(cr -> cr.getProductUid().equals(primaryAccountUid.get())).findFirst()
                            .orElse(null);
                    if (creditDto != null) {
                        if (paymentSystem.get() != null && cardNumber.get() != null) {
                            output.add("beCard", getCardNameForBot(paymentSystem.get(), cardNumber.get()));
                            dialogLogInfo(dialogData, getCardNameForBot(paymentSystem.get(), cardNumber.get()));
                        } else {
                            dialogLogWarn(dialogData, "Отсутствует платёжня система или номер карты.");
                        }
                        dialogLog(dialogData, "Параметры карты " + c);
                        dialogLog(dialogData, "Параметры счёта " + creditDto);
                    }
                });

        if (output.get().size() > 0) {
            output.add("beFindCardsCreditFinanceStatus", "ok");
        } else {
            output.add("beFindCardsCreditFinanceStatus", "notFound");
        }
        return output.get();
    }

    private static boolean findCreditsInfo(String primaryAccountUid, OutputMap output, DialogData dialogData) {
        CreditDto creditDto = dialogData.finance.getData().getCredits().stream()
                .filter(cr -> cr.getProductUid().equals(primaryAccountUid)).findFirst()
                .orElse(null);
        if (creditDto != null) {
            Double amount = creditDto.getAmount();
            Double accountAmount = creditDto.getAccountAmount();
            Currency currency = creditDto.getCurrency();
            Double cardAmount = null;
            String contractNumber = creditDto.getContractNumber();
            String paymentDate = creditDto.getSchedulePayment().getPaymentDate();
            Double fullAmount = creditDto.getSchedulePayment().getFullAmount();
            String gracePeriodEndDate = creditDto.getGracePeriod().getGracePeriodEndDate();
            Double gracePeriodAmount = creditDto.getGracePeriod().getGracePeriodAmount();

            if (amount != null) {
                cardAmount = amount;
            } else if (accountAmount != null) {
                cardAmount = accountAmount;
            }
            if (accountAmount != null && currency != null) {
                output.add("BEcardAmount", cardAmount + " " + toCurrencySymbol(currency.toString()));
                if (cardAmount < 0) {
                    output.add("BEcardAmountNegative", "true");
                } else if (cardAmount == 0) {
                    output.add("BEcardAmountZero", "true");
                }
            } else {
                dialogLogWarn(dialogData, "Отсутствуют остаток и сумма на счёте или валюта счёта.");
                output.add("BEgetCardFinanceStatus", "error");
                return true;
            }
            if (contractNumber != null) {
                output.add("BEcardContractNumber", contractNumber);
            }
            if (paymentDate != null) {
                output.add("BEcardPaymentDate", convertDate(paymentDate));
            }
            if (fullAmount != null) {
                output.add("BEcardFullAmount", fullAmount + " " + toCurrencySymbol(currency.toString()));
            }
            if (gracePeriodEndDate != null) {
                output.add("BEcardGracePeriodEndDate", convertDate(gracePeriodEndDate));
            }
            if (gracePeriodAmount != null) {
                output.add("BEcardGracePeriodAmount", gracePeriodAmount
                        + " " + toCurrencySymbol(currency.toString()));
            }
        }
        return false;
    }

    private static String convertDate(String oldDate) {
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Format newDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String newDate = null;

        if (oldDate != null) {
            try {
                newDate = newDateFormat.format(oldDateFormat.parse(oldDate));
            } catch (ParseException e) {
                genericLogWarn("Ошибка при конвертации даты: " + e);
            }
        }
        return newDate;
    }

    /**
     * BackEndCall BEfindCreditCardTariffStatus Получение тарифов по кредитной карте
     */
    public static Map<String, ApiField> findCreditCardTariff(String cardName, DialogData dialogData) {
        genericLogInfo("Выполняется findCreditCardTariff");
        OutputMap output = new OutputMap();
        final String cardPan;
        AtomicReference<String> listOfUnusedTariffs = new AtomicReference<>();

        if (cardName != null && cardName.matches(".*\\d{4}$")) {
            cardPan = getCardPanLast4Dig(cardName);
        } else {
            dialogLogWarn(dialogData, "Некорректно указаны последние 4 цифры номера платёжной карты: "
                    + cardName);
            output.add("BEfindCreditCardTariffStatus", "badRequest");
            return output.get();
        }
        if (dialogData.cards == null) {
            dialogLogWarn(dialogData, "Отсутствует список карт клиента");
            output.add("BEfindCreditCardTariffStatus", "error");
            return output.get();
        }

        String cardId = dialogData.cards.getData().stream()
                .filter(c -> cardPan.equals(getCardPanLast4Dig(c.getCardNumber()))).findFirst()
                .map(GetCardsResponse::getId).map(Object::toString).orElse(null);
        if (cardId == null) {
            dialogLogWarn(dialogData, "Отсутствует ИД карты.");
            output.add("BEfindCreditCardTariffStatus", "badRequest");
            return output.get();
        }

        findFinance(dialogData, FinanceListType.NewFinance);
        if (dialogData.finance == null) {
            dialogLogWarn(dialogData, "Отсутствует список финансовых продуктов клиента");
            output.add("BEfindCreditCardTariffStatus", "error");
            return output.get();
        }

        String primaryAccountUid = dialogData.finance.getData().getCards().stream()
                .filter(c -> c.getProductId().equals(cardId)).findFirst().map(CardDto::getPrimaryAccountUid)
                .map(Object::toString).orElse(null);
        if (primaryAccountUid == null) {
            dialogLogWarn(dialogData, "Отсутствует primaryAccountUid карты.");
            output.add("BEfindCreditCardTariffStatus", "error");
            return output.get();
        }

        CreditDto creditDto = dialogData.finance.getData().getCredits().stream()
                .filter(c -> c.getProductUid().equals(primaryAccountUid)).findFirst().orElse(null);
        if (creditDto == null) {
            dialogLogWarn(dialogData, "Отсутствует счёт кредитной карты.");
            output.add("BEfindCreditCardTariffStatus", "error");
            return output.get();
        }

        ProductDetailsByIdResponse productDetails = findProductDetailsById(creditDto.getProductId(),
                creditDto.getProductType().toString(), dialogData);
        if (productDetails == null) {
            dialogLogWarn(dialogData, "Отсутствует детали продукта.");
            output.add("BEfindCreditCardTariffStatus", "error");
            return output.get();
        }

        dialogLogInfo(dialogData, "Для анализа тарифов КК:");
        dialogLog(dialogData, productDetails.getData().getTariffDetails().toString());
        productDetails.getData().getTariffDetails().forEach(t -> {
            String id = t.getId();

            if (id != null) {
                switch (id) {
                    case "firstMonth":
                    case "subsequentMonthFree":
                    case "subsequentMonth":
                    case "bankAndPartnersWithdrawalCommissions":
                    case "otherBanksWithdrawalCommissions":
                    case "transfersByCardNumberCommissions":
                    case "ownFundsTransfersByAccountDetailsCommissions":
                    case "creditFundsTransfersByAccountDetailsCommissions":
                    case "withdrawalAndP2PTransactionsUpToFixLimit":
                    case "withdrawalAndP2PTransactionsOverFixLimit":
                    case "transfersByAccountDetailsAndByPhoneNumber":
                    case "transfersBetweenAccountsWithinBank":
                    case "transfersOwnFundsBetweenAccountsWithinBank":
                    case "bankAndPartnersOwnFundsWithdrawalCommissions":
                    case "otherBanksOwnFundsWithdrawalCommissions":
                    case "ownFundsFromCardTransfersCommissions":
                    case "creditFundsTransfersAndWithdrawalFromCardCommissions":
                    case "creditFundsFromAccountTransfersCommissions":
                    case "transfersByCardNumberAndCashWithdrawals":
                        output.add("BE" + id, t.getName() + " - " + t.getValue());
                        break;
                    default:
                        if (listOfUnusedTariffs.get() == null) {
                            synchronized (listOfUnusedTariffs) {
                                if (listOfUnusedTariffs.get() == null) {
                                    listOfUnusedTariffs.set(id);
                                }
                            }
                        } else {
                            listOfUnusedTariffs.set(listOfUnusedTariffs + ", " + id);
                        }
                        break;
                }
                output.add("BElistOfUnusedTariffs", listOfUnusedTariffs.get());
            }
        });

        if (output.get().size() > 0) {
            output.add("BEfindCreditCardTariffStatus", "ok");
        } else {
            output.add("BEfindCreditCardTariffStatus", "notFound");
        }
        return output.get();
    }

    /**
     * Получение списка продуктов клиента втч с расширенным набором атрибутов.
     * С алгоритмом перезапросов при получении кодов 203 и 206.
     */
    public static void findFinance(DialogData dialogData, FinanceListType listType) {
        genericLogInfo("Выполняется findFinance");
        int quantity = 0;
        Integer responseDelay = null;
        String validityKey = null;
        int statusCode;

        while (quantity < 3) {
            dialogLog(dialogData, quantity + 1 + "-я итерация.");
            if (responseDelay != null) {
                dialogLog(dialogData, "Задержка: " + responseDelay + " сек.");
                try {
                    Thread.sleep(responseDelay * 1000L);
                } catch (Exception e) {
                    dialogLogWarn(dialogData, "Произошла ошибка во время паузы: " + e);
                }
            }
            if (listType.equals(FinanceListType.NewFinance)) {
                statusCode = requestNewFinance(validityKey, dialogData);
            } else {
                statusCode = requestFinanceExtended(validityKey, dialogData);
            }
            if (statusCode == HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION
                    || statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
                dialogLogWarn(dialogData, "В витрине " + listType + " не актуальные данные.");
                if (listType.equals(FinanceListType.NewFinance)) {
                    responseDelay = dialogData.finance.getData().getResponseDelay();
                    validityKey = dialogData.finance.getData().getValidityKey();
                } else {
                    responseDelay = dialogData.financeExtended.getData().getResponseDelay();
                    validityKey = dialogData.financeExtended.getData().getValidityKey();
                }
                if (responseDelay == null) {
                    responseDelay = 3;
                }
                if (validityKey == null) {
                    break;
                }
                quantity++;
                continue;
            }
            break;
        }
    }

    /**
     * Получение списка продуктов клиента.
     * Используется только внешними каналами.
     */
    public static int requestNewFinance(String validityKey, DialogData dialogData) {
        genericLogInfo("Выполняется requestNewFinance");
        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            return HttpStatus.SC_NOT_FOUND;
        }

        HttpGet request = new HttpGet(CustomConfig.properties.get("BspApiAddr").toString() + "/finance/v4");

        if (validityKey != null) {
            try {
                request.setUri(new URIBuilder(request.getUri()).addParameter("validityKey", validityKey)
                        .build());
            } catch (URISyntaxException e) {
                dialogLogWarn(dialogData, "Ошибка при получении URI: " + e);
            }
        }

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        BotHttpResponseDto response = invokeGet(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION
                || statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
            dialogData.finance = new Gson().fromJson(responseJson, ResponseNewFinanceDto.class);
            genericLog("ResponseNewFinanceDto:\n" + dialogData.finance);
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n" +
                    responseJson);
        }
        return statusCode;
    }

    /**
     * Получение списка продуктов клиента с расширенным набором атрибутов.
     * Используется только внутренними каналами.
     */
    private static int requestFinanceExtended(String validityKey, DialogData dialogData) {
        genericLogInfo("Выполняется requestFinanceExtended");
        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            return HttpStatus.SC_NOT_FOUND;
        }

        HttpGet request = new HttpGet(CustomConfig.properties.get("BspApiAddr").toString() +
                "/finance/v4/extended");

        try {
            if (validityKey != null) {
                request.setUri(new URIBuilder(request.getUri())
                        .addParameter("validityKey", validityKey)
                        .addParameter("withClosedProducts", "false")
                        .build());
            } else {
                request.setUri(new URIBuilder(request.getUri()).addParameter("withClosedProducts", "false")
                        .build());
            }
        } catch (URISyntaxException e) {
            dialogLogWarn(dialogData, "Ошибка при получении URI: " + e);
        }

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        BotHttpResponseDto response = invokeGet(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION
                || statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
            dialogData.financeExtended = new Gson().fromJson(responseJson, FinanceExtendedResponse.class);
            genericLog("FinanceExtendedResponse:\n" + dialogData.financeExtended);
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n" +
                    responseJson);
        }
        return statusCode;
    }

    /**
     * Получение деталей продукта с текстами, иконками, подсказками по Id.
     * Будет заменён методом getProductDetailsByUid
     */
    private static ProductDetailsByIdResponse findProductDetailsById(String productId, String type,
                                                                     DialogData dialogData) {
        genericLogInfo("Выполняется getProductDetailsById");
        if (!CustomerInfoService.isXAuthUserDefined(dialogData) || productId == null || type == null) {
            return ProductDetailsByIdResponse.builder().build();
        }

        HttpGet request = new HttpGet(CustomConfig.properties.get("BspApiAddr").toString() +
                "/finance/v4/" + productId + "/details");

        try {
            request.setUri(new URIBuilder(request.getUri()).addParameter("type", type).build());
        } catch (URISyntaxException e) {
            dialogLogWarn(dialogData, "Ошибка при получении URI: " + e);
        }
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        BotHttpResponseDto response = invokeGet(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK) {
            ProductDetailsByIdResponse details = new Gson().fromJson(responseJson, ProductDetailsByIdResponse.class);
            genericLog("ProductDetailsByIdResponse:\n" + details);
            return details;
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n" +
                    responseJson);
        }
        return ProductDetailsByIdResponse.builder().result(ResponseMetaData.builder().status(statusCode).build())
                .build();
    }

    public static void setIcrByFinance(DialogData dialogData) {
        genericLogInfo("Выполняется setIcrByFinance");
        if (dialogData.finance == null) {
            dialogLogWarn(dialogData, "Отсутствуют продукты у клиента.");
            return;
        }
        dialogData.finance.getData().getSavings().forEach(s -> {
            SavingsProductType type = s.getProductType();

            if (type.equals(SavingsProductType.CURRENT)) {
                dialogData.setFieldValue("ICRhasCurrentAccount", "true");
            }
            if (type.equals(SavingsProductType.DEPOSIT) || type.equals(SavingsProductType.ACCOUNT_SAVINGS)) {
                dialogData.setFieldValue("ICRhasSavingsAccount", "true");
            }
        });
        dialogData.finance.getData().getCredits().forEach(c -> {
            CreditProductType type = c.getProductType();
            Double pastDue = c.getPastDuePayment().getPastDuePaymentAmount();

            if (type.equals(CreditProductType.ACCOUNT_OVERDRAFT)) {
                dialogData.setFieldValue("ICRhasOverdraft", "true");
            }
            if (type.equals(CreditProductType.UNAUTHORIZED_OVERDRAFT)) {
                dialogData.setFieldValue("icrHasUnauthorizedOverdraft", "true");
            }
            if (type.equals(CreditProductType.CREDIT_B)) {
                dialogData.setFieldValue("ICRhasPotrebCredit", "true");
            }
            if (pastDue != null && pastDue > 0) {
                dialogData.setFieldValue("ICRpastDue", "true");
            }
            dialogData.setFieldValue("ICRhasAnyCredit", "true");
        });
        dialogData.finance.getData().getMortgages().stream().filter(s -> s.getState().equals(State.ACTIVE))
                .forEach(m -> {
            Double pastDue = m.getPastDuePrincipalAmount();

            if (pastDue != null && pastDue > 0) {
                dialogData.setFieldValue("ICRpastDue", "true");
            }
            dialogData.setFieldValue("ICRhasMortgage", "true");
            dialogData.setFieldValue("ICRhasAnyCredit", "true");
        });
        dialogData.finance.getData().getCarloans().forEach(c -> {
            Double pastDue = c.getPastDuePayment().getPastDuePaymentAmount();

            if (pastDue != null && pastDue > 0) {
                dialogData.setFieldValue("ICRpastDue", "true");
            }
            dialogData.setFieldValue("ICRhasAutoCredit", "true");
            dialogData.setFieldValue("ICRhasAnyCredit", "true");
        });
    }
}
