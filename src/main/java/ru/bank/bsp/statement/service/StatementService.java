package ru.bank.bsp.statement.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.omilia.diamant.dialog.components.fields.ApiField;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.bot.utils.OutputMap;
import ru.bank.client.dto.BotHttpResponseDto;
import ru.bank.bsp.customerinfo.service.CustomerInfoService;
import ru.bank.bsp.statement.dto.ResponseListStatementDto;
import ru.bank.bsp.statement.dto.ResponsePersonalAvailableStatementsDto;
import ru.bank.bsp.statement.dto.StatementDto;
import ru.bank.bsp.statement.model.CreatePersonalStatementRequest;
import ru.bank.bsp.statement.model.ErrorResult;
import ru.bank.bsp.statement.model.PersonalStatementParams;
import ru.bank.bsp.statement.model.PersonalStatementProductInfo;
import ru.bank.bsp.statement.model.ProductType;
import ru.bank.bsp.statement.model.ResponseMetaData;
import ru.bank.bsp.statement.model.ResponseVoid;
import ru.bank.bsp.statement.model.StatementType;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static ru.bank.bot.service.BotService.toCurrencySymbol;
import static ru.bank.client.BotHttpClient.invokeGet;
import static ru.bank.client.BotHttpClient.invokePost;
import static ru.bank.util.Utils.dialogLog;
import static ru.bank.util.Utils.dialogLogInfo;
import static ru.bank.util.Utils.dialogLogWarn;
import static ru.bank.util.Utils.genericLog;
import static ru.bank.util.Utils.genericLogInfo;

/**
 * Statement-API Сервис заказа справок и выписок
 */
public class StatementService {
    /**
     * BackEndCall Получение списка доступных для формирования справок
     */
    public static Map<String, ApiField> findPersonalAvailableStatements(StatementType type, DialogData dialogData) {
        genericLogInfo("Выполняется findPersonalAvailableStatements");
        OutputMap output = new OutputMap();
        AtomicInteger counter = new AtomicInteger(1);

        if (!CustomerInfoService.isXAuthUserDefined(dialogData) || type == null) {
            output.add("BEfindPersonalAvailableStatementsStatus", "error");
            return output.get();
        }

        HttpGet request = new HttpGet(CustomConfig.properties.get("BspApiAddr").toString() +
                "/statement/v0/personalAvailableStatements");

        try {
            request.setUri(new URIBuilder(request.getUri()).addParameter("types", type.toString()).build());
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
            dialogData.personalAvailableStatements = new Gson().fromJson(responseJson,
                    ResponsePersonalAvailableStatementsDto.class);
            genericLog("ResponsePersonalAvailableStatementsDto:\n" + dialogData.personalAvailableStatements);
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_CONFLICT
                || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
            return output.setStatusAndReturn("BEfindPersonalAvailableStatementsStatus", "error");
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n" +
                    responseJson);
            return output.setStatusAndReturn("BEfindPersonalAvailableStatementsStatus", "error");
        }

        // TODO сделать передачу списка форматов файла во флоу БОТа
        dialogData.personalAvailableStatements.getData().getCategories()
                .forEach(c -> c.getStatements()
                        .forEach(s -> s.getProducts()
                                .forEach(p -> {
                                    output.add("BEpersonalAvailableStatement" + counter.get() + "Name",
                                            p.getClearedName());
                                    output.add("BEpersonalAvailableStatement" + counter.get() + "Amount",
                                            p.getAmount().toString());
                                    output.add("BEpersonalAvailableStatement" + counter.getAndIncrement() +
                                            "Currency", toCurrencySymbol(p.getCurrency().toString()));
                                })));

        if (output.size() == 0) {
            dialogLog(dialogData, "Отсутствуют доступные для формирования справки");
        }

        output.add("BEfindPersonalAvailableStatementsStatus", "ok");
        return output.get();
    }

    /**
     * BackEndCall Получение информации по заказанным справкам
     */
    public static Map<String, ApiField> findListAllStatements(DialogData dialogData) {
        genericLogInfo("Выполняется findListAllStatements");
        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            return null;
        }

        HttpGet request = new HttpGet(CustomConfig.properties.get("BspApiAddr").toString() + "/statement/v0");

        try {
            request.setUri(new URIBuilder(request.getUri()).addParameter("onlyReleased", "false").build());
        } catch (URISyntaxException e) {
            dialogLogWarn(dialogData, "Ошибка при получении URI: " + e);
        }

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        BotHttpResponseDto response = invokeGet(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();
        OutputMap output = new OutputMap();
        AtomicInteger counter = new AtomicInteger(1);

        if (statusCode == HttpStatus.SC_OK) {
            dialogData.listStatement = new Gson().fromJson(responseJson, ResponseListStatementDto.class);
            genericLog("ResponseListStatementDto:\n" + dialogData.listStatement);
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_CONFLICT
                || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
            return output.setStatusAndReturn("BEfindListAllStatementsStatus", "error");
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n" +
                    responseJson);
            return output.setStatusAndReturn("BEfindListAllStatementsStatus", "error");
        }

        dialogData.listStatement.getData().forEach(s -> {
            String name = s.getName();
            String format = s.getFileFormat();
            StatementType type = s.getType();
            String expire = s.getExpireDate();
            String create = s.getCreateDate();
            String status = s.getStatus();
            String description = s.getDescription();
            String message = s.getMessage();
            String url = s.getUrl();

            if (name != null && !name.isEmpty()) {
                output.add("BEstatement" + counter.get() + "Name", name);
            }
            if (format != null && !format.isEmpty()) {
                output.add("BEstatement" + counter.get() + "FileFormat", format);
            }
            if (type != null) {
                output.add("BEstatement" + counter.get() + "Type", type.toString());
            }
            if (expire != null && !expire.isEmpty()) {
                output.add("BEstatement" + counter.get() + "ExpireDate", expire);
            }
            if (create != null && !create.isEmpty()) {
                output.add("BEstatement" + counter.get() + "CreateDate", create);
            }
            if (status != null && !status.isEmpty()) {
                output.add("BEstatement" + counter.get() + "Status", status);
            }
            if (description != null && !description.isEmpty()) {
                output.add("BEstatement" + counter.get() + "Description", description);
            }
            if (message != null && !message.isEmpty()) {
                output.add("BEstatement" + counter.get() + "Message", message);
            }
            if (url != null && !url.isEmpty()) {
                output.add("BEstatement" + counter.getAndIncrement() + "Url", url);
            }
        });

        if (output.size() == 0) {
            dialogLog(dialogData, "Отсутствует информация по всем заказанным справкам");
        }

        output.add("BEfindListAllStatementsStatus", "ok");
        return output.get();
    }

    /**
     * Создание заявки на формирование справки
     */
    private static ResponseVoid requestPersonalStatement(CreatePersonalStatementRequest statementRequest,
                                                         DialogData dialogData) {
        genericLogInfo("Выполняется requestPersonalStatement");
        if (!CustomerInfoService.isXAuthUserDefined(dialogData)) {
            return ResponseVoid.builder().build();
        }

        HttpPost request = new HttpPost(CustomConfig.properties.get("BspApiAddr").toString()
                + "/statement/v0/personal");

        request.setEntity(new StringEntity((new Gson()).toJson(statementRequest)));
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader("X-auth-user", dialogData.xAuthUser);

        BotHttpResponseDto response = invokePost(request, dialogData);
        int statusCode = response.getStatusCode();
        JsonElement responseJson = response.getResponseJson();

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
            ResponseVoid responseVoid = new Gson().fromJson(responseJson, ResponseVoid.class);
            genericLog("ResponseVoid:\n" + responseVoid);
            return responseVoid;
        } else if (statusCode == HttpStatus.SC_CLIENT_ERROR || statusCode == HttpStatus.SC_CONFLICT
                || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            ErrorResult errorResult = new Gson().fromJson(responseJson, ErrorResult.class);
            dialogLogWarn(dialogData, "ErrorResult:\n" + errorResult);
        } else {
            dialogLogWarn(dialogData, "Непредусмотренный код состояния HTTP: " + statusCode + ", Json:\n" +
                    responseJson);
        }
        return ResponseVoid.builder().result(ResponseMetaData.builder().status(statusCode).build()).build();
    }

    /**
     * BackEndCall Выписка по счёту (выписка документом)
     */
    public static Map<String, ApiField> requestAccountPersonalStatement(DialogData dialogData, String statementMame,
                                                                        String startDate, String endDate,
                                                                        String extension) {
        dialogLogInfo(dialogData, "BackEndCall requestAccountPersonalStatement получение выписки по счёту");

        StatementType type = StatementType.ACCOUNT_STATEMENT;
        AtomicReference<String> id = new AtomicReference<>();
        AtomicReference<ProductType> productType = new AtomicReference<>();
        String rangeStartDate;
        String rangeEndDate;
        CreatePersonalStatementRequest request;
        ResponseVoid response;
        OutputMap output = new OutputMap();

        try {
            rangeStartDate = Objects.requireNonNull(convertDate(startDate)).toString();
        } catch (ParseException e) {
            dialogLogWarn(dialogData, "Ошибка: " + e + "\nПри конвертации даты: startDate=" + startDate);
            return output.setStatusAndReturn("BErequestAccountPersonalStatementStatus", "error");
        }
        try {
            rangeEndDate = Objects.requireNonNull(convertDate(endDate)).toString();
        } catch (ParseException e) {
            dialogLogWarn(dialogData, "Ошибка: " + e + "\nПри конвертации даты: endDate=" + endDate);
            return output.setStatusAndReturn("BErequestAccountPersonalStatementStatus", "error");
        }

        // TODO сделать валидацию списка форматов файла данными из класса ResponsePersonalAvailableStatementsDto
        //  сделать формат файла ENUM

        if (!extension.equalsIgnoreCase("PDF") && !extension.equalsIgnoreCase("CSV")) {
            dialogLogWarn(dialogData, "Ошибка в формате файла: extension=" + extension);
            return output.setStatusAndReturn("BErequestAccountPersonalStatementStatus", "error");
        }

        dialogData.personalAvailableStatements.getData().getCategories().forEach(c -> c.getStatements()
                .forEach(s -> s.getProducts().forEach(p -> {
                    if (p.getClearedName().equalsIgnoreCase(statementMame)) {
                        id.set(p.getId());
                        productType.set(p.getType());
                    }
                })));

        request = CreatePersonalStatementRequest.builder()
                .initSystem("DBO")
                .extension(extension.toUpperCase())
                .type(type)
                .params(PersonalStatementParams.builder()
                        .language("RUS")
                        .products(Collections.singletonList(PersonalStatementProductInfo.builder()
                                .id(id.get())
                                .type(productType.get())
                                .build()))
                        .createProductDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .rangeStartDate(rangeStartDate)
                        .rangeEndDate(rangeEndDate)
                        .build())
                .build();
        response = requestPersonalStatement(request, dialogData);

        if (response != null && !response.getResult().getStatus().equals(HttpStatus.SC_OK)
                && !response.getResult().getStatus().equals(HttpStatus.SC_CREATED)) {
            return output.setStatusAndReturn("BErequestAccountPersonalStatementStatus", "error");
        }

        dialogLog(dialogData, "Создана заявка на формирование справки с типом " + type);
        output = findListStatementsAfterNSeconds(10, 5, 55, type, dialogData);
        output.add("BErequestAccountPersonalStatementStatus", "ok");
        return output.get();
    }

    /**
     * BackEndCall Выписка для государственных служащих
     */
    public static Map<String, ApiField> requestCivilServantsPersonalStatement(String civilServantDate,
                                                                              DialogData dialogData) {
        genericLogInfo("Выполняется requestCivilServantsPersonalStatement");

        StatementType type = StatementType.CIVIL_SERVANTS;
        String endDate;
        CreatePersonalStatementRequest request;
        ResponseVoid response;
        OutputMap output = new OutputMap();

        try {
            endDate = Objects.requireNonNull(convertDate(civilServantDate)).toString();
        } catch (ParseException e) {
            dialogLogWarn(dialogData, "Ошибка: " + e + "\nПри конвертации даты: civilServantDate="
                    + civilServantDate);
            return output.setStatusAndReturn("BErequestCivilServantsPersonalStatementStatus", "error");
        }

        request = CreatePersonalStatementRequest.builder()
                .initSystem("DBO")
                .extension("PDF")
                .type(type)
                .params(PersonalStatementParams.builder()
                        .language("RUS")
                        .products(null)
                        .createProductDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .rangeStartDate(null)
                        .rangeEndDate(endDate)
                        .build())
                .build();
        response = requestPersonalStatement(request, dialogData);

        if (response != null && !response.getResult().getStatus().equals(HttpStatus.SC_OK)
                && !response.getResult().getStatus().equals(HttpStatus.SC_CREATED)) {
            return output.setStatusAndReturn("BErequestCivilServantsPersonalStatementStatus", "error");
        }

        dialogLog(dialogData, "Создана заявка на формирование справки с типом: " + type);
        output = findListStatementsAfterNSeconds(10, 5, 55, type, dialogData);
        output.add("BErequestCivilServantsPersonalStatementStatus", "ok");
        return output.get();
    }

    /**
     * BackEndCall Получение информации по заказанным справкам с задержкой времени
     */
    public static Map<String, ApiField> findListStatementsWithTimeDelay(StatementType type, DialogData dialogData) {
        genericLogInfo("Выполняется findListStatementsWithTimeDelay");
        OutputMap output = findListStatementsAfterNSeconds(10, 5, 55, type, dialogData);

        if (output.size() == 0) {
            output.add("BEfindListStatementsWithTimeDelayStatus", "error");
        } else {
            output.add("BEfindListStatementsWithTimeDelayStatus", "ok");
        }
        return output.get();
    }

    /**
     * Информация по заказанным справкам после n-секундной задержки
     */
    public static OutputMap findListStatementsAfterNSeconds(int delayInit, int delayInterval, int delayLim,
                                                            StatementType type, DialogData dialogData) {
        dialogLog(dialogData, "Получение информации по заказанным справкам.");

        int delayTotal = delayInit;
        int iteration = 1;
        StatementDto statementDto = null;
        String currentDateMinusOneDay = LocalDate.now().minusDays(1).format(DateTimeFormatter
                .ofPattern("yyyy-MM-dd"));
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String currentDatePlusOneDay = LocalDate.now().plusDays(1).format(DateTimeFormatter
                .ofPattern("yyyy-MM-dd"));
        OutputMap output = new OutputMap();

        while (delayTotal <= delayLim) {
            dialogLog(dialogData, iteration++ + "-я итерация. Задержка: " + delayInit + " сек.");

            try {
                Thread.sleep(delayInit * 1000L);
            } catch (Exception e) {
                dialogLogWarn(dialogData, "Произошла ошибка во время паузы: " + e);
            }

            if (iteration == 2) {
                delayInit = delayInterval;
            }
            delayTotal = delayTotal + delayInterval;

            if (Objects.requireNonNull(findListAllStatements(dialogData)).get("BEfindListAllStatementsStatus")
                    .getValue().equals("ok")) {
                statementDto = dialogData.listStatement.getData().stream()
                        .filter(s -> s.getType().equals(type))
                        .filter(s -> s.getCreateDate().equals(currentDateMinusOneDay) ||
                                s.getCreateDate().equals(currentDate) ||
                                s.getCreateDate().equals(currentDatePlusOneDay))
                        .findFirst().orElse(null);
            } else {
                dialogLogWarn(dialogData, "Ошибка сервиса получения информации по всем заказанным справкам.");
                break;
            }

            if (statementDto != null) {
                genericLog("Последняя заказанная справка:\n" + statementDto);
                if ((statementDto.getStatus().equals("EXECUTED") || statementDto.getStatus().equals("CREATED")) &&
                        statementDto.getUrl() == null) {
                    dialogLog(dialogData, "URL отсутствует.");
                } else {
                    dialogLog(dialogData, "Информация получена.");
                    break;
                }
            } else {
                dialogLog(dialogData, "Информация отсутствует.");
            }
        }

        if (statementDto != null) {
            output.add("BEstatement1Name", statementDto.getName());
            output.add("BEstatement1FileFormat", statementDto.getFileFormat());
            output.add("BEstatement1ExpireDate", statementDto.getExpireDate());
            output.add("BEstatement1Status", statementDto.getStatus());
            output.add("BEstatement1Description", statementDto.getDescription());
            output.add("BEstatement1Message", statementDto.getMessage());

            String url = statementDto.getUrl();
            if (url != null) {
                output.add("BEstatement1Url",
                        "ChatButtonDownload(" + url.trim().substring(url.trim().length() - 8) + ")");
            }
        }

        if (output.size() == 0) {
            dialogLog(dialogData, "Отсутствует информация по заказанным справкам с типом: " + type +
                    " на дату: " + currentDate + " (+/- 1 день)");
        }

        return output;
    }

    // TODO написать универсальный метод валидации дат для выписок взамен 3-х:
    //  validationStartOrEndDateStatementStatus, validationDatesStatementStatus, validationCivilServantDateStatus

    /**
     * Валидация даты выписки для начала и конца периода
     */
    public static Map<String, ApiField> validationStartOrEndDateStatementStatus(String backEndCallName, String dateIn) {
        OutputMap output = new OutputMap();
        LocalDate startDate;
        LocalDate today = LocalDate.now();

        try {
            startDate = convertDate(dateIn);
        } catch (ParseException e) {
            return output.setStatusAndReturn(backEndCallName, "Ошибка формата даты: " + dateIn);
        }

        if (startDate != null && startDate.isBefore(today.minusYears(5)))
            return output.setStatusAndReturn(backEndCallName,
                    "Выписку можно сформировать только за последние 5 лет.");
        if (startDate != null && startDate.isAfter(ChronoLocalDate.from(LocalDateTime.now())))
            return output.setStatusAndReturn(backEndCallName, "Дата не может быть позже " +
                    today.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ".");
        return output.setStatusAndReturn(backEndCallName, "ok");
    }

    /**
     * Валидация дат
     */
    public static Map<String, ApiField> validationDatesStatementStatus(String startDate, String endDate) {
        OutputMap output = new OutputMap();
        LocalDate start;
        LocalDate end;

        try {
            start = convertDate(startDate);
        } catch (ParseException e) {
            return output.setStatusAndReturn("BEvalidationDatesStatementStatus",
                    "Ошибка формата даты начала периода: " + startDate);
        }
        try {
            end = convertDate(endDate);
        } catch (ParseException e) {
            return output.setStatusAndReturn("BEvalidationDatesStatementStatus",
                    "Ошибка формата даты конца периода: " + endDate);
        }

        if (start != null && start.isAfter(end))
            return output.setStatusAndReturn("BEvalidationDatesStatementStatus",
                    "Дата начала периода не может быть позже даты конца периода.");

        return output.setStatusAndReturn("BEvalidationDatesStatementStatus", "ok");
    }

    /**
     * Валидация отчётной даты для заказа справки для госслужащих через Task Tracker
     */
    public static Map<String, ApiField> validationCivilServantDateStatus(String dateIn) {
        OutputMap output = new OutputMap();
        LocalDate date;
        LocalDate start = LocalDate.of(Year.now().minusYears(1).getValue(), 12, 31);
        LocalDate todayMinusOneDay = LocalDate.now().minusDays(1);

        try {
            date = convertDate(dateIn);
        } catch (ParseException e) {
            return output.setStatusAndReturn("BEvalidationDateStatementStatus",
                    "Пожалуйста, укажите дату в формате: " +
                            todayMinusOneDay.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }

        if (date != null && date.isBefore(start))
            return output.setStatusAndReturn("BEvalidationCivilServantDateStatus",
                    "Отчётная дата не может быть раньше " +
                            start.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ".");
        if (date != null && date.isAfter(todayMinusOneDay))
            return output.setStatusAndReturn("BEvalidationCivilServantDateStatus",
                    "Отчётная дата не может быть позже " +
                            todayMinusOneDay.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ".");

        return output.setStatusAndReturn("BEvalidationCivilServantDateStatus", "ok");
    }

    private static LocalDate convertDate(String dateIn) throws ParseException {
        // TODO Заменить replaceAll'ы регулярным выражением
        String date = dateIn.replaceAll(",", ".").replaceAll("-", ".")
                .replaceAll("/", ".");
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yy"));
            } catch (DateTimeParseException ex) {
                return null;
            }
        }
    }
}
