package ru.bank.bsp.card.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.omilia.diamant.dialog.components.fields.ApiField;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.bot.utils.OutputMap;
import ru.bank.client.dto.BotHttpResponseDto;
import ru.bank.bsp.card.dto.ChangeCardStatusRequest;
import ru.bank.bsp.card.dto.ChangeCardStatusResult;
import ru.bank.bsp.card.dto.GetCardLimitResult;
import ru.bank.bsp.card.dto.GetCardsResponse;
import ru.bank.bsp.card.dto.GetCardsResult;
import ru.bank.bsp.card.dto.Result;
import ru.bank.bsp.card.model.ChangeStatus;
import ru.bank.bsp.card.model.ErrorResult;
import ru.bank.bsp.card.model.LimitType;
import ru.bank.bsp.card.model.OperationType;
import ru.bank.bsp.card.model.RequestType;
import ru.bank.bsp.card.model.Status;
import ru.bank.bsp.card.model.Type;
import ru.bank.bsp.customerinfo.service.CustomerInfoService;
import ru.bank.bsp.finance.dto.CardDto;
import ru.bank.bsp.finance.dto.CardExtendedDto;
import ru.bank.bsp.finance.model.FinanceListType;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static ru.bank.bot.service.BotService.toCurrencySymbol;
import static ru.bank.client.BotHttpClient.invokeGet;
import static ru.bank.client.BotHttpClient.invokePatch;
import static ru.bank.bsp.finance.service.FinanceService.findFinance;
import static ru.bank.util.Utils.*;

/**
 * Card-API Сервис для работы с картами клиента
 */
public class CardService {
    /**
     * Получение списка карт клиента
     */
    public static void findCards(DialogData dialogData) {
        genericLogInfo("Выполняется findCards");
        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            return;
        }

        HttpGet request = new HttpGet(CustomConfig.properties.get("BspApiAddr").toString() + "/card/v3");

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        BotHttpResponseDto response = invokeGet(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK) {
            dialogData.cards = new Gson().fromJson(responseJson, GetCardsResult.class);
            genericLog("GetCardsResult:\n" + dialogData.cards);
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_NOT_FOUND
                || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n"
                    + responseJson);
        }
    }

    /**
     * Получить список лимитов карты
     */
    public static GetCardLimitResult findCardLimit(String cardId, DialogData dialogData) {
        genericLogInfo("Выполняется findCardLimit");
        if (!CustomerInfoService.isXAuthUserDefined(dialogData) || cardId == null) {
            return GetCardLimitResult.builder().build();
        }

        HttpGet request = new HttpGet(CustomConfig.properties.get("BspApiAddr").toString() + "/card/v3/" + cardId
                + "/limit");

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        BotHttpResponseDto response = invokeGet(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK) {
            GetCardLimitResult cardLimit = new Gson().fromJson(responseJson, GetCardLimitResult.class);
            genericLog("GetCardLimitResult:\n" + cardLimit);
            return cardLimit;
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_NOT_FOUND
                || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n"
                    + responseJson);
        }
        return GetCardLimitResult.builder().result(Result.builder().status(statusCode).build()).build();
    }

    /**
     * Изменить статус карты
     */
    public static ChangeCardStatusResult changeCardStatus(String cardId, ChangeStatus status, DialogData dialogData) {
        genericLogInfo("Выполняется changeCardStatus: " + status);
        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            return ChangeCardStatusResult.builder().build();
        }

        HttpPatch request = new HttpPatch(CustomConfig.properties.get("BspApiAddr").toString() + "/card/v3/"
                + cardId + "/status");

        request.setEntity(new StringEntity((new Gson()).toJson(ChangeCardStatusRequest.builder().status(status)
                .build())));
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);

        BotHttpResponseDto response = invokePatch(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK) {
            ChangeCardStatusResult result = new Gson().fromJson(responseJson, ChangeCardStatusResult.class);
            genericLog("ChangeCardStatusResult:\n" + result);
            return result;
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_NOT_FOUND
                || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n"
                    + responseJson);
        }
        return ChangeCardStatusResult.builder().result(Result.builder().status(statusCode).build()).build();
    }

    /**
     * Получение списка карт для BackEndCall BEgetCardsStatus
     */
    public static Map<String, ApiField> getCards(RequestType type, DialogData dialogData) {
        genericLogInfo("Выполняется getCards с типом: " + type.toString());
        OutputMap output = new OutputMap();
        AtomicInteger i = new AtomicInteger(1);

        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            output.add("BEgetCardsStatus", "error");
            return output.get();
        }
        findCards(dialogData);
        if (dialogData.cards == null) {
            output.add("BEgetCardsStatus", "error");
            return output.get();
        }
        switch (type) {
            case ALL:
                dialogData.cards.getData().forEach(c -> {
                    output.add("BEcard" + i.get(), getCardNameForBot(c.getPaymentSystem(), c.getCardNumber()));
                    output.add("BEcardStatus" + i.get(), c.getStatus().toString());
                    output.add("BEcardType" + i.getAndIncrement(), c.getType().toString());
                });
                break;
            case DEBIT:
                dialogData.cards.getData().stream().filter(c -> c.getType().equals(Type.DEBIT)).forEach(c -> {
                    output.add("BEcard" + i.get(), getCardNameForBot(c.getPaymentSystem(), c.getCardNumber()));
                    output.add("BEcardStatus" + i.get(), c.getStatus().toString());
                    output.add("BEcardType" + i.getAndIncrement(), c.getType().toString());
                });
                break;
            case CREDIT:
                dialogData.cards.getData().stream().filter(c -> c.getType().equals(Type.CREDIT)).forEach(c -> {
                    output.add("BEcard" + i.get(), getCardNameForBot(c.getPaymentSystem(), c.getCardNumber()));
                    output.add("BEcardStatus" + i.get(), c.getStatus().toString());
                    output.add("BEcardType" + i.getAndIncrement(), c.getType().toString());
                });
                break;
            case BLOCK:
                dialogData.cards.getData().stream()
                        .filter(c -> c.getStatus().equals(Status.ACTIVE)
                                || c.getStatus().equals(Status.MONTH_BEFORE_EXPIRE))
                        .forEach(c -> {
                            output.add("BEcard" + i.get(),
                                    getCardNameForBot(c.getPaymentSystem(), c.getCardNumber()));
                            output.add("BEcardStatus" + i.get(), c.getStatus().toString());
                            output.add("BEcardType" + i.getAndIncrement(), c.getType().toString());
                        });
                break;
            case UNBLOCK:
                findFinance(dialogData, FinanceListType.NewFinance);
                findFinance(dialogData, FinanceListType.FinanceExtended);

                dialogData.cards.getData().stream()
                        .filter(c -> c.getStatus().equals(Status.BLOCKED_BY_BANK)
                                || c.getStatus().equals(Status.BLOCKED_BY_USER) || c.getStatus().equals(Status.CLOSED))
                        .forEach(c -> {
                            String cardUid = dialogData.finance.getData().getCards().stream()
                                    .filter(f -> f.getProductId().equals(c.getId()))
                                    .findFirst().map(CardDto::getProductUid).map(Object::toString).orElse(null);
                            String blockCode = dialogData.financeExtended.getData().getCards().stream()
                                    .filter(f -> f.getProductUid().equals(cardUid))
                                    .findFirst().map(CardExtendedDto::getBlockCode).map(Object::toString).orElse(null);
                            output.add("BEcard" + i.get(),
                                    getCardNameForBot(c.getPaymentSystem(), c.getCardNumber()));
                            output.add("BEcardStatus" + i.get(), c.getStatus().toString());
                            output.add("BEcardType" + i.get(), c.getType().toString());
                            output.add("BEcardBlockCode" + i.getAndIncrement(), blockCode);
                        });
                break;
        }
        if (output.get().size() > 0) {
            output.add("BEgetCardsStatus", "ok");
        } else {
            output.add("BEgetCardsStatus", "notFound");
        }
        return output.get();
    }

    /**
     * BackEndCall BEblockOrUnblockCardStatus Блокировка или разблокировка карты
     */
    public static Map<String, ApiField> blockOrUnblockCard(String cardName, ChangeStatus status, DialogData dialogData) {
        genericLogInfo("Выполняется blockOrUnblockCard: " + status);

        OutputMap output = new OutputMap();
        final String cardPan;
        AtomicReference<String> cardId = new AtomicReference<>();

        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            output.add("BEblockOrUnblockCardStatus", "error");
            return output.get();
        }
        if (cardName != null && cardName.matches(".*\\d{4}$")) {
            cardPan = getCardPanLast4Dig(cardName);
        } else {
            dialogLogWarn(dialogData, "Некорректно указаны последние 4 цифры номера платёжной карты: "
                    + cardName);
            output.add("BEblockOrUnblockCardStatus", "badRequest");
            return output.get();
        }
        if (dialogData.cards == null) {
            dialogLogWarn(dialogData, "Отсутствует список карт клиента");
            output.add("BEblockOrUnblockCardStatus", "error");
            return output.get();
        }
        switch (status) {
            case BLOCK:
                dialogData.cards.getData().stream()
                        .filter(c -> c.getStatus().equals(Status.ACTIVE)
                                || c.getStatus().equals(Status.MONTH_BEFORE_EXPIRE))
                        .filter(c -> cardPan.equals(getCardPanLast4Dig(c.getCardNumber())))
                        .forEach(c -> {
                            cardId.set(c.getId());
                            output.add("BEcard",
                                    getCardNameForBot(c.getPaymentSystem(), c.getCardNumber()));
                            output.add("BEcardStatus", c.getStatus().toString());
                            output.add("BEcardType", c.getType().toString());
                        });
                break;
            case UNBLOCK:
                dialogData.cards.getData().stream()
                        .filter(c -> c.getStatus().equals(Status.BLOCKED_BY_BANK)
                                || c.getStatus().equals(Status.BLOCKED_BY_USER)
                                || c.getStatus().equals(Status.CLOSED))
                        .filter(c -> cardPan.equals(getCardPanLast4Dig(c.getCardNumber())))
                        .forEach(c -> {
                            cardId.set(c.getId());
                            output.add("BEcard",
                                    getCardNameForBot(c.getPaymentSystem(), c.getCardNumber()));
                            output.add("BEcardStatus", c.getStatus().toString());
                            output.add("BEcardType", c.getType().toString());
                        });
                String cardUid = dialogData.finance.getData().getCards().stream()
                        .filter(f -> f.getProductId().equals(cardId.get()))
                        .findFirst().map(CardDto::getProductUid).map(Object::toString).orElse(null);
                String blockCode = dialogData.financeExtended.getData().getCards().stream()
                        .filter(f -> f.getProductUid().equals(cardUid))
                        .findFirst().map(CardExtendedDto::getBlockCode).map(Object::toString).orElse(null);
                if (blockCode != null && !blockCode.equals("IB") && !blockCode.equals("57")) {
                    dialogLogWarn(dialogData, "Нельзя разблокировать карту с кодом блокировки: " + blockCode);
                    output.add("BEblockOrUnblockCardStatus", blockCode);
                    return output.get();
                }
                break;
        }
        if (cardId.get() == null) {
            dialogLogWarn(dialogData, "Нет подходящей карты.");
            output.add("BEblockOrUnblockCardStatus", "notFound");
            return output.get();
        }

        ChangeCardStatusResult result = changeCardStatus(cardId.get(), status, dialogData);
        if (result.getResult().getStatus().equals(HttpStatus.SC_OK)
                && result.getResult().getCode().equals("CRDX.00000")) {
            output.add("BEblockOrUnblockCardStatus", "ok");
        } else {
            output.add("BEblockOrUnblockCardStatus", "error");
        }
        return output.get();
    }

    /**
     * BackEndCall BEfindCardLimitsStatus Получение списка лимитов карты
     */
    public static Map<String, ApiField> findCardLimits(String cardName, DialogData dialogData) {
        genericLogInfo("Выполняется findCardLimits");

        OutputMap output = new OutputMap();
        final String cardPan;
        GetCardLimitResult cardLimit;
        AtomicReference<String> currency = new AtomicReference<>();
        AtomicReference<Double> daily = new AtomicReference<>();
        AtomicReference<Double> monthly = new AtomicReference<>();
        AtomicReference<String> limit = new AtomicReference<>();

        if (cardName != null && cardName.matches(".*\\d{4}$")) {
            cardPan = getCardPanLast4Dig(cardName);
        } else {
            dialogLogWarn(dialogData, "Некорректно указаны последние 4 цифры номера платёжной карты: "
                    + cardName);
            output.add("BEfindCardLimitsStatus", "badRequest");
            return output.get();
        }
        if (dialogData.cards == null) {
            dialogLogWarn(dialogData, "Отсутствует список карт клиента");
            output.add("BEfindCardLimitsStatus", "error");
            return output.get();
        }

        String cardId = dialogData.cards.getData().stream()
                .filter(c -> cardPan.equals(getCardPanLast4Dig(c.getCardNumber()))).findFirst()
                .map(GetCardsResponse::getId).map(Object::toString).orElse(null);

        if (cardId != null) {
            cardLimit = findCardLimit(cardId, dialogData);
        } else {
            dialogLogWarn(dialogData, "Отсутствует ИД карты.");
            output.add("BEfindCardLimitsStatus", "badRequest");
            return output.get();
        }

        dialogLog(dialogData, cardLimit.getData().getLimit().toString());

        cardLimit.getData().getLimit().stream()
                .filter(Objects::nonNull)
                .filter(l -> Objects.nonNull(l.getOperationType()))
                .filter(l -> l.getOperationType().equals(OperationType.CASH_WITHDRAWAL))
                .forEach(l -> {
                    LimitType limitType = l.getLimitType();
                    String currencyCode = l.getCurrencyCode();
                    Double limitAmount = l.getLimitAmount();
                    Double expendedAmount = l.getExpendedAmount();

                    if (currencyCode != null) {
                        currency.set(l.getCurrencyCode());
                    }
                    Double amount = null;
                    if (limitAmount != null && expendedAmount != null) {
                        amount = l.getLimitAmount() - l.getExpendedAmount();
                    }
                    if (limitAmount != null && expendedAmount == null) {
                        amount = l.getLimitAmount();
                    }
                    if (amount != null) {
                        limit.set(amount.toString().replaceAll("\\.0+$", "") + " "
                                + toCurrencySymbol(currency.get()));
                    }
                    if (limitType != null && limitType.equals(LimitType.MONTHLY)) {
                        monthly.set(amount);
                        output.add("BEcardLimitsCashWithdrawalMonthly", limit.get());
                    } else if (limitType != null && limitType.equals(LimitType.DAILY)) {
                        daily.set(amount);
                        output.add("BEcardLimitsCashWithdrawalDaily", limit.get());
                    }
                });

        Double monthlyLimit = monthly.get();
        Double dailyLimit = daily.get();
        String curr = currency.get();

        if (monthlyLimit != null && dailyLimit != null && curr != null) {
            output.add("BEcardLimitsCashWithdrawalToday",
                    ((monthlyLimit <= dailyLimit) ? monthlyLimit : dailyLimit).toString()
                            .replaceAll("\\.0+$", "") + " " + toCurrencySymbol(curr));
        }

        cardLimit.getData().getLimit().stream()
                .filter(Objects::nonNull)
                .filter(l -> Objects.nonNull(l.getOperationType()))
                .filter(l -> l.getOperationType().equals(OperationType.CARD_TRANSFER))
                .forEach(l -> {
                    LimitType limitType = l.getLimitType();
                    String currencyCode = l.getCurrencyCode();
                    Double limitAmount = l.getLimitAmount();
                    Double expendedAmount = l.getExpendedAmount();

                    if (currencyCode != null) {
                        currency.set(l.getCurrencyCode());
                    }
                    Double amount = null;
                    if (limitAmount != null && expendedAmount != null) {
                        amount = l.getLimitAmount() - l.getExpendedAmount();
                    }
                    if (limitAmount != null && expendedAmount == null) {
                        amount = l.getLimitAmount();
                    }
                    if (amount != null) {
                        limit.set(amount.toString().replaceAll("\\.0+$", "") + " "
                                + toCurrencySymbol(currency.get()));
                    }
                    if (limitType != null && limitType.equals(LimitType.MONTHLY)) {
                        output.add("BEcardLimitsCardTransferMonthly", limit.get());
                    }
                });

        if (output.get().size() > 0) {
            output.add("BEfindCardLimitsStatus", "ok");
        } else {
            output.add("BEfindCardLimitsStatus", "notFound");
        }
        return output.get();
    }

    public static void setIcrByCard(DialogData dialogData) {
        genericLogInfo("Выполняется setIcrByCard");
        if (dialogData.cards == null) {
            dialogLogWarn(dialogData, "Отсутствуют карты у клиента.");
            return;
        }
        if (!dialogData.cards.getData().isEmpty()) {
            dialogData.setFieldValue("ICRhasCard", "true");
        }

        dialogData.cards.getData().forEach(c -> {
            Type type = c.getType();
            Status status = c.getStatus();

            if (type != null && type.equals(Type.DEBIT)) {
                dialogData.setFieldValue("ICRhasDebitCard", "true");
            }
            if (type != null && type.equals(Type.CREDIT)) {
                dialogData.setFieldValue("ICRhasCreditCard", "true");
                dialogData.setFieldValue("ICRhasAnyCredit", "true");
            }
            if (status != null && (status.equals(Status.ORDERED) || status.equals(Status.IN_PRODUCTION)
                    || status.equals(Status.DELIVERY_TO_DEPARTMENT) || status.equals(Status.DELIVERY_BY_COURIER)
                    || status.equals(Status.IN_DEPARTMENT))) {
                dialogData.setFieldValue("ICRnewCard", status.toString());
            }
        });
    }

    public static String getCardNameForBot(String paymentSystem, String cardNumber) {
        String number = cardNumber.replaceAll("\\s", "");
        return paymentSystem + " *" + number.substring(number.length() - 4);
    }

    public static String getCardPanLast4Dig(String cardName) {
        if (cardName != null && cardName.length() > 4) {
            cardName = cardName.substring(cardName.length() - 4);
        }
        return cardName;
    }
}
