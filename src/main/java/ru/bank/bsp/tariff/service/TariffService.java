package ru.bank.bsp.tariff.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.omilia.diamant.dialog.components.fields.ApiField;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.bot.utils.OutputMap;
import ru.bank.client.dto.BotHttpResponseDto;
import ru.bank.bsp.customerinfo.service.CustomerInfoService;
import ru.bank.bsp.tariff.dto.ErrorResult;
import ru.bank.bsp.tariff.dto.TariffResult;

import java.util.Map;

import static ru.bank.client.BotHttpClient.invokeGet;
import static ru.bank.util.Utils.dialogLog;
import static ru.bank.util.Utils.dialogLogWarn;
import static ru.bank.util.Utils.genericLog;
import static ru.bank.util.Utils.genericLogInfo;
import static ru.bank.util.Utils.stringToCamelCase;

/**
 * Tariff-API Сервис для получения деталей тарифа
 */
public class TariffService {
    /**
     * Получение деталей тарифов клиента
     */
    static void getTariff(DialogData dialogData) {
        genericLogInfo("Выполняется getTariff");
        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            return;
        }

        HttpGet request = new HttpGet(CustomConfig.properties.get("BspApiAddr").toString() + "/tariff-api/v0/tariff");

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        BotHttpResponseDto response = invokeGet(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            dialogData.tariff = new Gson().fromJson(responseJson, TariffResult.class);
            genericLog("TariffResult:\n" + dialogData.tariff);
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_NOT_FOUND
                || statusCode == HttpStatus.SC_METHOD_NOT_ALLOWED
                || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n" +
                    responseJson);
        }
    }

    /**
     * BackEndCall findTariff Получение групп комиссий из деталей тарифа
     */
    public static Map<String, ApiField> findTariff(DialogData dialogData) {
        genericLogInfo("Выполняется findTariff");
        OutputMap output = new OutputMap();

        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            output.add("BEfindTariffStatus", "error");
            return output.get();
        }

        getTariff(dialogData);
        if (dialogData.tariff == null) {
            dialogLogWarn(dialogData, "Отсутствуют детали тарифа клиента");
            output.add("BEfindTariffStatus", "error");
            return output.get();
        }

        dialogLog(dialogData, "Группы комиссий:");
        dialogData.tariff.getData().forEach(t -> t.getGroup().forEach(g -> g.getOperations().forEach(o -> {
            String additionalValue = o.getAdditionalValue();
            String value = (additionalValue == null) ? o.getMainValue() : o.getMainValue() + ", " + additionalValue;
            String operationType = o.getOperationType();

            if (operationType != null) {
                String type = "BE" + stringToCamelCase(operationType);
                dialogLog(dialogData, type + ": '" + value + "'");
                output.add(type, value);
            }
        })));

        if (output.get().size() > 0) {
            output.add("BEfindTariffStatus", "ok");
        } else {
            dialogLog(dialogData, "Тарифы клиента: " + dialogData.tariff);
            output.add("BEfindTariffStatus", "notFound");
        }
        return output.get();
    }
}