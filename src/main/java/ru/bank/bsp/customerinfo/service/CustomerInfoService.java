package ru.bank.bsp.customerinfo.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.client.dto.BotHttpResponseDto;
import ru.bank.bsp.customerinfo.dto.BisIdDtoFeign;
import ru.bank.bsp.customerinfo.dto.ResponseUserInfoResponseDto;
import ru.bank.bsp.customerinfo.model.ErrorResult;

import static ru.bank.client.BotHttpClient.invokeGet;
import static ru.bank.util.Utils.dialogLogWarn;
import static ru.bank.util.Utils.genericLog;
import static ru.bank.util.Utils.genericLogInfo;

/**
 * Customerinfo-API Сервис для получения информации по клиенту, используемой в логике мобильного приложения
 */
public class CustomerInfoService {

    public static void getXauthUser(DialogData dialogData) {
        genericLogInfo("Выполняется getXauthUser");
        dialogData.siebelId = dialogData.getFieldValue("siebelId");
        if (dialogData.siebelId == null) {
            dialogLogWarn(dialogData, "Отсутствует siebelId");
            return;
        }

        HttpGet request = new HttpGet(CustomConfig.properties.get("BspApiAddr").toString()
                + "/customerinfo/v1/crm/" + dialogData.siebelId);

        request.setHeader(HttpHeaders.ACCEPT, "application/json");

        BotHttpResponseDto response = invokeGet(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK) {
            dialogData.userInfo = new Gson().fromJson(responseJson, ResponseUserInfoResponseDto.class);
            genericLog("ResponseUserInfoResponseDto:\n" + dialogData.userInfo);
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n" +
                    responseJson);
        }
        if (dialogData.userInfo != null) {
            dialogData.xAuthUser = dialogData.userInfo.getData().getXauthUser();
        } else {
            dialogLogWarn(dialogData, "Отсутствует заголовок X-auth-user.");
        }
    }

    public static Boolean isXAuthUserDefined(DialogData dialogData) {
        if (dialogData.xAuthUser == null) {
            dialogLogWarn(dialogData, "Отсутствует заголовок X-auth-user. Запрос не выполнен.");
            return false;
        }
        return true;
    }

    public static void setIcrByCustomerInfo(DialogData dialogData) {
        genericLogInfo("Выполняется setIcrByCustomerInfo");
        if (dialogData.userInfo == null) {
            dialogLogWarn(dialogData, "Отсутствует информация по клиенту.");
            return;
        }

        String clientSegment = dialogData.userInfo.getData().getClientSegmentGroup();
        String servicePackage = dialogData.userInfo.getData().getCustomerIds().stream()
                .filter(i -> i.getServicePackage() != null).findFirst().map(BisIdDtoFeign::getServicePackage)
                .map(Object::toString).orElse(null);

        if (clientSegment != null) {
            dialogData.setFieldValue("ICRclientsSegment", clientSegment);
        }
        if (!dialogData.userInfo.getData().getCustomerIds().isEmpty()) {
            dialogData.setFieldValue("ICRhasBisId", "true");
        }
        if (servicePackage != null) {
            dialogData.setFieldValue("ICRservicePackage", servicePackage);
        }
    }
}
