package ru.bank.bsp.claims.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.omilia.diamant.dialog.components.fields.ApiField;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.bot.utils.OutputMap;
import ru.bank.client.dto.BotHttpResponseDto;
import ru.bank.bsp.claims.dto.ClaimsIssueDataDto;
import ru.bank.bsp.claims.dto.ClaimsIssueDto;
import ru.bank.bsp.claims.dto.ClaimsIssueExecDataDto;
import ru.bank.bsp.claims.dto.ResponseClaimsIssueDto;
import ru.bank.bsp.claims.dto.ResponseClaimsIssueExecDto;
import ru.bank.bsp.claims.model.ErrorResult;
import ru.bank.bsp.claims.model.ResponseMetaData;
import ru.bank.bsp.customerinfo.service.CustomerInfoService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import static ru.bank.client.BotHttpClient.invokePatch;
import static ru.bank.client.BotHttpClient.invokePost;
import static ru.bank.bsp.card.service.CardService.getCardPanLast4Dig;
import static ru.bank.util.Utils.dialogLog;
import static ru.bank.util.Utils.dialogLogGreen;
import static ru.bank.util.Utils.dialogLogInfo;
import static ru.bank.util.Utils.dialogLogWarn;
import static ru.bank.util.Utils.genericLog;
import static ru.bank.util.Utils.genericLogInfo;

/**
 * Claims-API Сервис для работы с заявлениями в свободной форме
 */
public class ClaimsService {

    /**
     * Создание заявки для заявления
     */
    private static ResponseClaimsIssueDto createIssue(DialogData dialogData) {
        genericLogInfo("createIssue Создание заявки для заявления");
        dialogLogInfo(dialogData, "createIssue Создание заявки для заявления");
        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            return ResponseClaimsIssueDto.builder().build();
        }

        HttpPost request = new HttpPost(CustomConfig.properties.get("BspApiAddr").toString() + "/claims/v1/issue");

        request.setEntity(new StringEntity((new Gson()).toJson(ClaimsIssueDataDto.builder()
                .operationType("ClaimsFreeForm")
                .operationRequestId("ChatBot"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")))
                .requestCreateDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")))
                .build())));
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);

        BotHttpResponseDto response = invokePost(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            ResponseClaimsIssueDto responseClaimsIssue = new Gson().fromJson(responseJson,
                    ResponseClaimsIssueDto.class);
            genericLog(responseClaimsIssue.toString());
            dialogLog(dialogData, responseClaimsIssue.toString());
            return responseClaimsIssue;
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_UNAUTHORIZED
                || statusCode == HttpStatus.SC_FORBIDDEN || statusCode == HttpStatus.SC_NOT_FOUND
                || statusCode == HttpStatus.SC_CONFLICT || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, errorResult.toString());
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n" +
                    responseJson);
        }
        return ResponseClaimsIssueDto.builder().build();
    }

    /**
     * Отправить заявку на выполнение
     */
    private static ResponseClaimsIssueExecDto executeIssue(ClaimsIssueExecDataDto claimsIssueExecData, String issueId,
                                                      DialogData dialogData) {
        genericLogInfo("executeIssue Отправка заявки на выполнение\nid: " + issueId + "\n" + claimsIssueExecData);
        dialogLogInfo(dialogData, "executeIssue Отправка заявки на выполнение\nid: " + issueId + "\n"
                + claimsIssueExecData);
        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            return ResponseClaimsIssueExecDto.builder().build();
        }

        HttpPatch request = new HttpPatch(CustomConfig.properties.get("BspApiAddr").toString()
                + "/claims/v1/issue/" + issueId + "/executed");

        request.setEntity(new StringEntity((new Gson()).toJson(claimsIssueExecData), StandardCharsets.UTF_8));
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);

        BotHttpResponseDto response = invokePatch(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NO_CONTENT) {
            ResponseClaimsIssueExecDto responseClaimsIssueExec = new Gson().fromJson(responseJson,
                    ResponseClaimsIssueExecDto.class);
            genericLog(responseClaimsIssueExec.toString());
            dialogLog(dialogData, responseClaimsIssueExec.toString());
            return responseClaimsIssueExec;
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_UNAUTHORIZED
                || statusCode == HttpStatus.SC_FORBIDDEN || statusCode == HttpStatus.SC_NOT_FOUND
                || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, errorResult.toString());
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n"
                    + responseJson);
        }
        return ResponseClaimsIssueExecDto.builder().result(ResponseMetaData.builder().status(statusCode).build())
                .build();
    }

    /**
     * Создание заявки в TaskTracker по закрытию КК с аннулированным лимитом
     */
    public static Map<String, ApiField> claimsIssueCardCreditClose(String cardName, DialogData dialogData) {
        genericLogInfo("claimsIssueCardCreditClose Создание заявки в TaskTracker по закрытию КК с аннулированным "
                + "лимитом");
        dialogLogInfo(dialogData, "claimsIssueCardCreditClose Создание заявки в TaskTracker по закрытию КК " +
                "с аннулированным лимитом");
        OutputMap output = new OutputMap();
        String description;

        if (cardName == null || cardName.isEmpty()) {
            dialogLogWarn(dialogData, "Отсутствует имя КК: " + cardName);
            output.add("beClaimsIssueCardCreditCloseStatus", "error");
            return output.get();
        }

        ResponseClaimsIssueDto responseClaimsIssue = createIssue(dialogData);
        String id = getId(responseClaimsIssue);
        String fullName = findIssueUserFullName(responseClaimsIssue);

        if (id == null) {
            dialogLogWarn(dialogData, "Не создана заявка для заявления.");
            output.add("beClaimsIssueCardCreditCloseStatus", "error");
            return output.get();
        }
        if (fullName.isEmpty()) {
            dialogLogWarn(dialogData, "В ДБО отсутствует ФИО.");
            output.add("beClaimsIssueCardCreditCloseStatus", "error");
            return output.get();
        }
        if (cardName.equalsIgnoreCase("all")) {
            description = "Я, " + fullName + ", прошу закрыть все кредитные карты.";
        } else {
            description = "Я, " + fullName + ", прошу закрыть кредитную карту " + cardName + ".";
        }

        ResponseClaimsIssueExecDto responseClaimsIssueExec = executeIssue(ClaimsIssueExecDataDto.builder()
                        .description(description)
                        .claimsTypeCode("CLOSING_CREDIT_CARD")
                        .notToCall(true)
                        .attachments(Collections.emptyList())
                        .build(),
                id, dialogData);

        if (responseClaimsIssueExec == null) {
            dialogLogWarn(dialogData, "Заявка " + id + " на закрытие КК не отправлена.");
            output.add("beClaimsIssueCardCreditCloseStatus", "error");
            return output.get();
        }

        ResponseMetaData result = responseClaimsIssueExec.getResult();
        if (result == null) {
            dialogLogWarn(dialogData, "Заявка " + id + " на закрытие КК не отправлена.");
            output.add("beClaimsIssueCardCreditCloseStatus", "error");
            return output.get();
        }

        String code = result.getCode();
        if (code != null && code.equals("CLMS.00000")) {
            dialogLogGreen(dialogData, "Отправлена заявка " + id + " на закрытие КК: " + description);
            output.add("beClaimsIssueCardCreditCloseStatus", "ok");
        } else {
            dialogLogWarn(dialogData, "Заявка " + id + " на закрытие КК не отправлена.");
            output.add("beClaimsIssueCardCreditCloseStatus", "error");
        }
        return output.get();
    }

    /**
     * Создание заявки в TaskTracker по закрытию овердрафтов
     */
    public static Map<String, ApiField> claimsIssueCardOverdraftClose(String accountName, DialogData dialogData) {
        genericLogInfo("claimsIssueCardOverdraftClose Создание заявки в TaskTracker по закрытию овердрафтов");
        dialogLogInfo(dialogData, "claimsIssueCardOverdraftClose Создание заявки в TaskTracker по закрытию "
                + "овердрафтов");
        OutputMap output = new OutputMap();
        String description;

        if (accountName == null || accountName.isEmpty()) {
            dialogLogWarn(dialogData, "Отсутствует счёт: " + accountName);
            output.add("beClaimsIssueCardOverdraftCloseStatus", "error");
            return output.get();
        }

        ResponseClaimsIssueDto responseClaimsIssue = createIssue(dialogData);
        String id = getId(responseClaimsIssue);
        String fullName = findIssueUserFullName(responseClaimsIssue);

        if (id == null) {
            dialogLogWarn(dialogData, "Не создана заявка для заявления.");
            output.add("beClaimsIssueCardOverdraftCloseStatus", "error");
            return output.get();
        }
        if (fullName.isEmpty()) {
            dialogLogWarn(dialogData, "В ДБО отсутствует ФИО.");
            output.add("beClaimsIssueCardOverdraftCloseStatus", "error");
            return output.get();
        }
        if (accountName.equalsIgnoreCase("all")) {
            description = "Я, " + fullName + ", прошу закрыть все овердрафты.";
        } else {
            description = "Я, " + fullName + ", прошу закрыть овердрафт по текущему счёту *"
                    + getCardPanLast4Dig(accountName) + ".";
        }

        ResponseClaimsIssueExecDto responseClaimsIssueExec = executeIssue(ClaimsIssueExecDataDto.builder()
                        .description(description)
                        .claimsTypeCode("CLOSING_CREDIT_CARD")
                        .notToCall(true)
                        .attachments(Collections.emptyList())
                        .build(),
                id, dialogData);

        if (responseClaimsIssueExec == null) {
            dialogLogWarn(dialogData, "Заявка " + id + " на закрытие КК не отправлена.");
            output.add("beClaimsIssueCardOverdraftCloseStatus", "error");
            return output.get();
        }

        ResponseMetaData result = responseClaimsIssueExec.getResult();
        if (result == null) {
            dialogLogWarn(dialogData, "Заявка " + id + " на закрытие овердрафта не отправлена.");
            output.add("beClaimsIssueCardOverdraftCloseStatus", "error");
            return output.get();
        }

        String code = result.getCode();
        if (code != null && code.equals("CLMS.00000")) {
            dialogLogGreen(dialogData, "Отправлена заявка " + id + " на закрытие овердрафта: " + description);
            output.add("beClaimsIssueCardOverdraftCloseStatus", "ok");
        } else {
            dialogLogWarn(dialogData, "Заявка " + id + " на закрытие овердрафта не отправлена.");
            output.add("beClaimsIssueCardOverdraftCloseStatus", "error");
        }
        return output.get();
    }

    /**
     * Создание заявки в TaskTracker по заказу справки для госслужащих
     */
    public static Map<String, ApiField> claimsIssueCivilServant(String date, DialogData dialogData) {
        genericLogInfo("claimsIssueCivilServant Создание заявки в TaskTracker по заказу справки для госслужащих");
        dialogLogInfo(dialogData, "claimsIssueCivilServant Создание заявки в TaskTracker по заказу справки "
                + "для госслужащих");
        OutputMap output = new OutputMap();

        if (date == null || date.isEmpty()) {
            dialogLogWarn(dialogData, "Отсутствует отчётная дата: " + date);
            output.add("beClaimsIssueCivilServantStatus", "error");
            return output.get();
        }

        ResponseClaimsIssueDto responseClaimsIssue = createIssue(dialogData);
        String id = getId(responseClaimsIssue);
        String fullName = findIssueUserFullName(responseClaimsIssue);

        if (id == null) {
            dialogLogWarn(dialogData, "Не создана заявка для заявления.");
            output.add("beClaimsIssueCivilServantStatus", "error");
            return output.get();
        }
        if (fullName.isEmpty()) {
            dialogLogWarn(dialogData, "В ДБО отсутствует ФИО.");
            output.add("beClaimsIssueCivilServantStatus", "error");
            return output.get();
        }

        String description = "Я, " + fullName + ", прошу подготовить справку для госслужащих за "
                + LocalDate.now().minusYears(1).getYear() + " год. Отчетная дата: " + date;
        ResponseClaimsIssueExecDto responseClaimsIssueExec = executeIssue(ClaimsIssueExecDataDto.builder()
                        .description(description)
                        .claimsTypeCode("OrderingCertificatesCivil")
                        .notToCall(true)
                        .attachments(Collections.emptyList())
                        .build(),
                id, dialogData);

        if (responseClaimsIssueExec == null) {
            dialogLogWarn(dialogData, "Заявка " + id + " на заказ справки для госслужащих не отправлена.");
            output.add("beClaimsIssueCivilServantStatus", "error");
            return output.get();
        }

        ResponseMetaData result = responseClaimsIssueExec.getResult();
        if (result == null) {
            dialogLogWarn(dialogData, "Заявка " + id + " на ззаказ справки для госслужащих не отправлена.");
            output.add("beClaimsIssueCivilServantStatus", "error");
            return output.get();
        }

        String code = result.getCode();
        if (code != null && code.equals("CLMS.00000")) {
            dialogLogGreen(dialogData, "Отправлена заявка " + id + " на заказ справки для госслужащих: "
                    + description);
            output.add("beClaimsIssueCivilServantStatus", "ok");
        } else {
            dialogLogWarn(dialogData, "Заявка " + id + " на заказ справки для госслужащих не отправлена.");
            output.add("beClaimsIssueCivilServantStatus", "error");
        }
        return output.get();
    }

    /**
     * Создание заявки в TaskTracker по закрытию счёта
     */
    public static Map<String, ApiField> claimsIssueCloseAccount(String account, String claimType,
                                                                String feedbackChannel, DialogData dialogData) {
        genericLogInfo("claimsIssueCloseAccount Создание заявки в TaskTracker по закрытию счёта");
        dialogLogInfo(dialogData, "claimsIssueCloseAccount Создание заявки в TaskTracker по закрытию счёта");
        OutputMap output = new OutputMap();
        String description;

        if (account == null || account.isEmpty()) {
            dialogLogWarn(dialogData, "Отсутствует счёт: " + account);
            output.add("beClaimsIssueCloseAccountStatus", "error");
            return output.get();
        }

        ResponseClaimsIssueDto responseClaimsIssue = createIssue(dialogData);
        String id = getId(responseClaimsIssue);
        String fullName = findIssueUserFullName(responseClaimsIssue);

        if (id == null) {
            dialogLogWarn(dialogData, "Не создана заявка для заявления.");
            output.add("beClaimsIssueCloseAccountStatus", "error");
            return output.get();
        }
        if (fullName.isEmpty()) {
            dialogLogWarn(dialogData, "В ДБО отсутствует ФИО.");
            output.add("beClaimsIssueCloseAccountStatus", "error");
            return output.get();
        }
        if (account.equalsIgnoreCase("all")) {
            description = "Я, " + fullName + ", прошу закрыть все счета.";
        } else {
            description = "Я, " + fullName + ", прошу закрыть счёт \"" + account + "\".";
        }
        if (feedbackChannel.equalsIgnoreCase("byPhone")) {
            description += " Связаться со мной можно по телефону.";
        } else {
            description += " Связаться со мной можно в чате.";
        }

        ResponseClaimsIssueExecDto responseClaimsIssueExec = executeIssue(ClaimsIssueExecDataDto.builder()
                        .description(description)
                        .claimsTypeCode((claimType.equals("ACCOUNT_CLOSE_BOT_SOLOCITY")) ? "ACCOUNT_CLOSE_BOT_SOLOCITY"
                                : "ACCOUNT_CLOSE_BOT")
                        .notToCall(true)
                        .attachments(Collections.emptyList())
                        .build(),
                id, dialogData);

        if (responseClaimsIssueExec == null) {
            dialogLogWarn(dialogData, "Заявка " + id + " на закрытие счёта не отправлена.");
            output.add("beClaimsIssueCloseAccountStatus", "error");
            return output.get();
        }

        ResponseMetaData result = responseClaimsIssueExec.getResult();
        if (result == null) {
            dialogLogWarn(dialogData, "Заявка " + id + " на закрытие счёта не отправлена.");
            output.add("beClaimsIssueCloseAccountStatus", "error");
            return output.get();
        }

        String code = result.getCode();
        if (code != null && code.equals("CLMS.00000")) {
            dialogLogGreen(dialogData, "Отправлена заявка " + id + " на закрытие счёта: " + description);
            output.add("beClaimsIssueCloseAccountStatus", "ok");
        } else {
            dialogLogWarn(dialogData, "Заявка " + id + " на закрытие счёта не отправлена.");
            output.add("beClaimsIssueCloseAccountStatus", "error");
        }
        return output.get();
    }

    private static String getId(ResponseClaimsIssueDto responseClaimsIssue) {
        if (responseClaimsIssue != null) {
            ClaimsIssueDto data = responseClaimsIssue.getData();
            if (data != null) {
                String issueId = data.getIssueId();
                if (issueId != null && !issueId.isEmpty()) {
                    return issueId;
                }
            }
        }
        return null;
    }

    private static String findIssueUserFullName(ResponseClaimsIssueDto responseClaimsIssue) {
        String fullName = "";

        if (responseClaimsIssue != null) {
            ClaimsIssueDto data = responseClaimsIssue.getData();

            if (data != null) {
                String lastName = data.getLastName();
                String firstName = data.getFirstName();
                String middleName = data.getMiddleName();

                if (lastName != null && !lastName.isEmpty()) {
                    fullName += lastName + " ";
                }
                if (firstName != null && !firstName.isEmpty()) {
                    fullName += firstName + " ";
                }
                if (middleName != null && !middleName.isEmpty()) {
                    fullName += middleName;
                }
            }
        }
        return fullName;
    }
}
