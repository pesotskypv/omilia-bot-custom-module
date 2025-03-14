package ru.bank.bsp.loyalty.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.client.dto.BotHttpResponseDto;
import ru.bank.bsp.customerinfo.service.CustomerInfoService;
import ru.bank.bsp.loyalty.dto.ResponseListLoyaltyProgramDto;
import ru.bank.bsp.loyalty.model.ErrorResult;
import ru.bank.bsp.loyalty.model.LoyaltyStatus;

import static ru.bank.client.BotHttpClient.invokeGet;
import static ru.bank.util.Utils.dialogLogWarn;
import static ru.bank.util.Utils.genericLog;
import static ru.bank.util.Utils.genericLogInfo;

/**
 * Loyalty-API сервис для операций с программой лояльности
 */
public class LoyaltyService {
    /**
     * Получение информации о подключенных ПЛ пользователя
     */
    public static void findLoyaltyPrograms(DialogData dialogData) {
        genericLogInfo("Выполняется findLoyaltyPrograms");
        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            return;
        }

        HttpGet request = new HttpGet(CustomConfig.properties.get("BspApiAddr").toString() + "/loyalty/v4");

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        BotHttpResponseDto response = invokeGet(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION
                || statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
            dialogData.loyaltyPrograms = new Gson().fromJson(responseJson, ResponseListLoyaltyProgramDto.class);
            genericLog("ResponseListLoyaltyProgramDto:\n" + dialogData.loyaltyPrograms);
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_UNAUTHORIZED
                || statusCode == HttpStatus.SC_FORBIDDEN || statusCode == HttpStatus.SC_NOT_FOUND
                || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n" +
                    responseJson);
        }
    }

    public static void setIcrByLoyalty(DialogData dialogData) {
        genericLogInfo("Выполняется setIcrByLoyalty");
        if (dialogData.loyaltyPrograms == null) {
            dialogLogWarn(dialogData, "Отсутствуют ПЛ у клиента.");
            return;
        }

        dialogData.loyaltyPrograms.getData().forEach(p -> p.getLoyalties().forEach(l -> {
            if (l.getLoyaltyStatus().equals(LoyaltyStatus.ACTIVE)) {
                switch (p.getProgram()) {
                    case CASHBACK:
                        dialogData.setFieldValue("ICRloyalityMoznovse", "true");
                        break;
                    case TRAVEL:
                        dialogData.setFieldValue("ICRloyalityTravel", "true");
                        break;
                    case AFFINITY:
                        dialogData.setFieldValue("ICRloyalityAffinity", "true");
                        break;
                    case OKEY:
                        dialogData.setFieldValue("ICRloyalityOkey", "true");
                        break;
                    case GOROD:
                        dialogData.setFieldValue("ICRloyalityGorod", "true");
                        break;
                    case OFFERS:
                        dialogData.setFieldValue("ICRloyalityOffers", "true");
                        break;
                }
            }
        }));
    }
}