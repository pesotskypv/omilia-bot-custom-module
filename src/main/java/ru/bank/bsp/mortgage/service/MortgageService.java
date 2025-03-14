package ru.bank.bsp.mortgage.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.client.dto.BotHttpResponseDto;
import ru.bank.bsp.customerinfo.service.CustomerInfoService;
import ru.bank.bsp.mortgage.dto.MortgageDetailsResult;
import ru.bank.bsp.mortgage.model.ErrorResult;

import static ru.bank.client.BotHttpClient.invokeGet;
import static ru.bank.util.Utils.dialogLogWarn;
import static ru.bank.util.Utils.genericLog;
import static ru.bank.util.Utils.genericLogInfo;

/**
 * Mortgage-API Сервис для работы с ипотечными кредитами
 */
public class MortgageService {
    /**
     * Получить детальную (расширенную) информацию по кредиту
     */
    public static MortgageDetailsResult getMortgageByProductUid(String productUid, DialogData dialogData) {
        genericLogInfo("Выполняется getMortgageByProductUid");
        if (!CustomerInfoService.isXAuthUserDefined(dialogData) || productUid == null) {
            return null;
        }

        HttpGet request = new HttpGet(CustomConfig.properties.get("BspApiAddr").toString() + "/mortgage/v1/" +
                productUid);

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        BotHttpResponseDto response = invokeGet(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK) {
            MortgageDetailsResult mortgageDetails = new Gson().fromJson(responseJson, MortgageDetailsResult.class);
            genericLog("MortgageDetailsResult:\n" + mortgageDetails);
            return mortgageDetails;
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n" +
                    responseJson);
        }
        return null;
    }
}
