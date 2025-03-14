package ru.bank;

import com.omilia.diamant.custommodule.CustomModuleAdaptor;
import com.omilia.diamant.custommodule.DataPooler;
import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.Field;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import com.omilia.diamant.dialog.components.fields.FieldsContainer;
import com.omilia.diamant.loggers.GenericLogger;
import com.omilia.diamant.managers.DialogManager;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.bot.Email;
import ru.bank.bot.HttpRequests;
import ru.bank.bot.Mfm;
import ru.bank.bot.Omni;
import ru.bank.bot.Outbound;
import ru.bank.bot.Auto;
import ru.bank.bot.Siebel;
import ru.bank.bot.SoapRequest;
import ru.bank.bot.SqlRequest;
import ru.bank.bot.nlu.Utterance;
import ru.bank.bot.service.BotService;
import ru.bank.bsp.card.model.RequestType;
import ru.bank.bsp.card.model.ChangeStatus;
import ru.bank.bsp.statement.model.StatementType;
import ru.bank.bsp.statement.service.StatementService;
import ru.bank.bsp.tariff.service.TariffService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static ru.bank.aid.service.CcReportService.getCcReport;
import static ru.bank.aid.service.CcReportService.getEmployeeLogin;
import static ru.bank.bot.doatm.Actions.getNearestOffices;
import static ru.bank.bot.service.BotService.getBalance;
import static ru.bank.bsp.card.service.CardService.blockOrUnblockCard;
import static ru.bank.bsp.card.service.CardService.getCards;
import static ru.bank.bsp.card.service.CardService.findCardLimits;
import static ru.bank.bsp.claims.service.ClaimsService.claimsIssueCardCreditClose;
import static ru.bank.bsp.claims.service.ClaimsService.claimsIssueCardOverdraftClose;
import static ru.bank.bsp.claims.service.ClaimsService.claimsIssueCivilServant;
import static ru.bank.bsp.claims.service.ClaimsService.claimsIssueCloseAccount;
import static ru.bank.bsp.finance.service.FinanceService.findCardsCreditFinance;
import static ru.bank.bsp.finance.service.FinanceService.findCreditCardTariff;
import static ru.bank.bsp.finance.service.FinanceService.getCardFinance;
import static ru.bank.bsp.finance.service.FinanceService.getCardOptions;
import static ru.bank.util.Utils.dialogLog;
import static ru.bank.util.Utils.dialogLogInfo;
import static ru.bank.util.Utils.dialogLogWarn;

public class BotModule extends CustomModuleAdaptor {
    public DialogData dialogData = new DialogData();

    // Срабатывает при развёртывании сценария
    @Override
    public boolean onApplicationStart(Map<String, Object> properties) {
        GenericLogger logger = DialogManager.getInstance().getLogger();
        logger.logInfo("Starting application...");
        logger.log("Properties from DiaManT_Custom_Config.xml: " + properties);
        CustomConfig.properties = properties; // Сохраняем значения из DiaManT_Custom_Config.xml
        CustomConfig.glogger = logger;
        CustomConfig.reloadContextRules(); // Загружаем контекстные правила
        return true;
    }

    public boolean onDialogStart(FieldsContainer fieldsContainer) {
        dialogData.fieldsContainer = fieldsContainer;
        dialogData.dlogger = this.logger;
        Thread getIcrThread = new Thread(() -> BotService.getIcr(dialogData));
        Thread getIcrDbLookupThread = new Thread(() -> SqlRequest.getIcrDbLookup(dialogData));
        Thread getRecentDialogsThread = new Thread(() -> Omni.getRecentDialogs(dialogData));

        dialogData.setFieldValue("BEcurrentDate", LocalDate.now().format(DateTimeFormatter
                .ofPattern("dd.MM.yyyy")));
        dialogData.setFieldValue("BEcurrentYear", String.valueOf(LocalDate.now().getYear()));
        dialogData.setFieldValue("BEcurrentMonth", String.valueOf(LocalDate.now().getMonthValue()));
        dialogData.setFieldValue("BEcurrentDay", String.valueOf(LocalDate.now().getDayOfMonth()));
        dialogData.setFieldValue("BEdayOfWeek", LocalDate.now().getDayOfWeek().toString());
        dialogData.setFieldValue("BEcurrentDateMinusOneDay", LocalDate.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        getIcrThread.start(); // Получение ICR из BSP
        getIcrDbLookupThread.start(); // Получение ICR из DBLookup
        getRecentDialogsThread.start(); // Получение количества недавних диалогов

        try {
            getIcrThread.join();
            getRecentDialogsThread.join();
            getIcrDbLookupThread.join();
        } catch (InterruptedException e) {
            dialogLogWarn(dialogData, "Произошла ошибка при выполнении Thread: " + e);
        }

        if (dialogData.finance != null) {
            dialogLogInfo(dialogData, "Список продуктов клиента:\nСчета:");
            dialogLog(dialogData, dialogData.finance.getData().getSavings().toString());
            if (dialogData.finance.getData().getCards().size() > 0) {
                dialogLogInfo(dialogData, "Карты:");
                dialogLog(dialogData, dialogData.finance.getData().getCards().toString());
            }
            if (dialogData.finance.getData().getCredits().size() > 0) {
                dialogLogInfo(dialogData, "Кредиты:");
                dialogLog(dialogData, dialogData.finance.getData().getCredits().toString());
            }
            if (dialogData.finance.getData().getMortgages().size() > 0) {
                dialogLogInfo(dialogData, "Ипотека:");
                dialogLog(dialogData, dialogData.finance.getData().getMortgages().toString());
            }
            if (dialogData.finance.getData().getCarloans().size() > 0) {
                dialogLogInfo(dialogData, "Автокредиты:");
                dialogLog(dialogData, dialogData.finance.getData().getCarloans().toString());
            }
            if (dialogData.finance.getData().getInsurances().size() > 0) {
                dialogLogInfo(dialogData, "Страховки:");
                dialogLog(dialogData, dialogData.finance.getData().getInsurances().toString());
            }
        }
        return true;
    }

    @Override
    public Map<String, ApiField> applyCustomAction(String function, Map<String, ApiField> input) {
        // Для использования в getOrDefault
        ApiField nullApiField = ApiField.builder().value(null).status(FieldStatus.DEFINED).build();
        Map<String, ApiField> output = new HashMap<>();
        ApiField result;

        switch (function) {
            case "getAnnounces":
                /*
                 * Ответы вне бота
                 * Выход: BEgetAnnouncesStatus, announce1-20, hasError
                 * */
                output = SqlRequest.getAnnounces(input.get("dtName").getValue(), input.get("id").getValue());
                break;
            case "getRequests":
                /*
                 * Ответы вне бота
                 * Выход: BEgetRequestsStatus, hasError, requestNormal[1-20], requestError[1-20],
                 * requestUnrecovered[1-20]
                 * */
                output = SqlRequest.getRequests(input.get("dtName").getValue(), input.get("id").getValue());
                break;
            case "getConfirms":
                /*
                 * Ответы вне бота
                 * Выход: BEgetConfirmsStatus, hasError, confirmNormal[1-20], confirmError[1-20],
                 * confirmUnrecovered[1-20]
                 * */
                output = SqlRequest.getConfirms(input.get("dtName").getValue(), input.get("id").getValue());
                break;
            case "getReactions": // Ответы вне бота
                output = SqlRequest.getReactions(input.get("eventName").getValue(),
                        input.get("outputFieldName").getValue());
                break;
            case "getRandom": // Случайное число от 1 до BErandomMax. Результат в BErandom.
                int BErandomMax = Integer.parseInt(input.get("BErandomMax").getValue());
                String a = String.valueOf(new Random().nextInt(BErandomMax) + 1);
                result = ApiField.builder().name("BErandom").value(a).status(FieldStatus.DEFINED).build();
                output.put("BErandom", result);
                break;
            case "selectRandomQuestions":
                /*
                 * Метод возвращает динамическое количество филдов, равное BEtotalQuestions.
                 * Филды называются по шаблону BEshowQuestion{x}, где {x} - случайное число от 1 до
                 * BEhowMuchQuestionsNeed
                 * */
                int BEtotalQuestions = Integer.parseInt(input.get("BEtotalQuestions").getValue());
                int BEhowMuchQuestionsNeed = Integer.parseInt(input.get("BEhowMuchQuestionsNeed").getValue());
                while (output.size() < BEhowMuchQuestionsNeed) {
                    String questionNumber = String.valueOf(new Random().nextInt(BEtotalQuestions) + 1);
                    result = ApiField.builder().name("BEshowQuestion" + questionNumber).value("1")
                            .status(FieldStatus.DEFINED).build();
                    output.put("BEshowQuestion" + questionNumber, result);
                }
                output.put("BEselectRandomQuestionsStatus", ApiField.builder().name("BEselectRandomQuestionsStatus")
                        .value("ok").status(FieldStatus.DEFINED).build());
                break;
            case "getNearestAtms":
                output = ru.bank.bot.doatm.Actions.getNearestAtms(dialogData,
                        input.getOrDefault("BEatmCity", nullApiField).getValue(),
                        input.getOrDefault("BEatmAddress", nullApiField).getValue(),
                        input.getOrDefault("BEatmBankOrAny", nullApiField).getValue(),
                        input.getOrDefault("BEatmService", nullApiField).getValue());
                break;
            case "getNearestOffices":
                /*
                 * Возвращает BEgetNearestOfficesStatus, BEofficeDistanceX, BEofficeAddressX, BEofficeLatitudeX,
                 * BEofficeLongitudeX, BEofficeClientCoords
                 * */
                output = getNearestOffices(dialogData, input.get("BEofficeCity").getValue(),
                        input.get("BEofficeAddress").getValue(), input.get("BEofficeService").getValue());
                break;
            case "getCards":
                output = getCards(RequestType.valueOf(input.get("BEgetCardsRequestType").getValue()), dialogData);
                break;
            case "blockOrUnblockCard":
                output = blockOrUnblockCard(input.get("cardPAN").getValue(),
                        ChangeStatus.valueOf(input.get("BEgetCardsRequestType").getValue()), dialogData);
                break;
            case "findCardLimits":
                output = findCardLimits(input.get("cardPAN").getValue(), dialogData);
                break;
            case "getCardFinance":
                output = getCardFinance(input.get("cardPAN").getValue(), dialogData);
                break;
            case "getCardOptions":
                output = getCardOptions(input.get("cardPAN").getValue(), dialogData);
                break;
            case "getCardLocations":
                /*
                 * Поиск карт в отделениях
                 * Выход: BEgetCardLocationsStatus, BEcardLocation{X}, BEcardLocationSchedule{X}
                 * */
                output = Siebel.getCardLocations(input.get("siebelId").getValue(), dialogData);
                break;
            case "getQueueLength":
                /*
                 * Размер очереди
                 * Выход: BEqueueLength, BEqueueThreshold, BEgetQueueLengthStatus
                 * */
                output = Omni.getQueueLength(input.get("ActivityId").getValue(), input.get("id").getValue(),
                        input.get("Intent").getValue(), input.get("BEqueueThresholdMedium").getValue(),
                        input.get("BEqueueThresholdHigh").getValue());
                break;
            case "sendLeadAuto":
                /*
                 * Отправка лида в Siebel
                 * Выход: BEsendLeadAutoStatus
                 * */
                output = Auto.sendLead(input.get("uFirstName").getValue(), input.get("uLastName").getValue(),
                        input.get("uPatronymic").getValue(), input.get("uBirthDate").getValue(),
                        input.get("uPhoneNumber").getValue(), input.get("uEmail").getValue(),
                        input.get("utmMedium").getValue());
                break;
            case "findTariff":
                output = TariffService.findTariff(dialogData);
                break;
            case "getOmniCustomerId": // Получение CustomerId (Omni)
                output = Omni.getCustomerId(input.get("activityIdNow").getValue());
                break;
            case "saveEduUserData":
                output = SqlRequest.saveEduUserData(input.get("BEomniCustomerId").getValue(),
                        input.get("FirstName").getValue(), input.get("LastName").getValue(),
                        input.get("ValidEmail").getValue());
                break;
            case "getEduData":
                output = SqlRequest.getEduData(input.get("BEomniCustomerId").getValue());
                break;
            case "sendConfirmationCode":
                output = Email.sendConfirmationCode(dialogData, input.get("ValidEmail").getValue());
                break;
            case "getCcReport":
                output = getCcReport(input.get("BEgetCcReportName").getValue(), dialogData);
                break;
            case "getBalance":
                output = getBalance(input.get("BEgetBalanceFilter").getValue(), dialogData);
                break;
            case "outboundImport":
                output = SoapRequest.outboundImport(input.get("uFirstName").getValue(),
                        input.get("uLastName").getValue(),
                        input.getOrDefault("uPatronymic", nullApiField).getValue(),
                        input.get("uPhoneNumber").getValue(), input.get("BEoutboundImportCampaignId").getValue(),
                        input.get("BEoutboundImportTimeZoneInfoName").getValue(),
                        input.getOrDefault("BEoutboundImportAttr1Name", nullApiField).getValue(),
                        input.getOrDefault("BEoutboundImportAttr1Value", nullApiField).getValue(),
                        input.getOrDefault("BEoutboundImportAttr2Name", nullApiField).getValue(),
                        input.getOrDefault("BEoutboundImportAttr2Value", nullApiField).getValue(),
                        input.getOrDefault("BEoutboundImportAttr3Name", nullApiField).getValue(),
                        input.getOrDefault("BEoutboundImportAttr3Value", nullApiField).getValue(),
                        input.getOrDefault("BEoutboundImportAttr4Name", nullApiField).getValue(),
                        input.getOrDefault("BEoutboundImportAttr4Value", nullApiField).getValue(),
                        input.getOrDefault("BEoutboundImportAttr5Name", nullApiField).getValue(),
                        input.getOrDefault("BEoutboundImportAttr5Value", nullApiField).getValue());
                break;
            case "waitForSeconds":
                output = BotService.waitForSeconds(input.get("BEwaitForSecondsInput").getValue());
                break;
            case "claimsIssueCloseAccount":
                output = claimsIssueCloseAccount(input.get("BEcloseAccount").getValue(),
                        input.get("BEcloseAccountClaimType").getValue(),
                        input.get("BEcloseAccountFeedbackChannel").getValue(), dialogData);
                break;
            case "sendAnswerToHr":
                output = HttpRequests.sendAnswerToHr(input.get("BEanswerToHr").getValue(), dialogData);
                break;
            case "getPmStatus":
                output = Omni.getPmStatus(input.get("RequestType").getValue(), dialogData);
                break;
            // Получение тарифа
            case "findCreditCardTariff":
                output = findCreditCardTariff(input.get("cardPAN").getValue(), dialogData);
                break;
            case "findPersonalAvailableStatements": // Получение списка доступных для формирования справок
                output = StatementService.findPersonalAvailableStatements(StatementType
                        .valueOf(input.get("BEfindPersonalAvailableStatementsFilter").getValue()), dialogData);
                break;
            case "findListAllStatements": // Получение информации по заказанным справкам
                output = StatementService.findListAllStatements(dialogData);
                break;
            case "findListStatementsWithTimeDelay": // Получение информации по заказанным справкам с задержкой времени
                output = StatementService.findListStatementsWithTimeDelay(StatementType
                        .valueOf(input.get("BEfindPersonalAvailableStatementsFilter").getValue()), dialogData);
                break;
            case "validationStartDateStatementStatus": // Валидация даты начала периода
                output = StatementService.validationStartOrEndDateStatementStatus(
                        "BEvalidationStartDateStatementStatus",
                        input.get("BEstatementRangeStartDate").getValue());
                break;
            case "validationEndDateStatementStatus": // Валидация даты конца периода
                output = StatementService.validationStartOrEndDateStatementStatus(
                        "BEvalidationEndDateStatementStatus",
                        input.get("BEstatementRangeEndDate").getValue());
                break;
            case "validationDatesStatementStatus": // Валидация дат
                output = StatementService.validationDatesStatementStatus(
                        input.get("BEstatementRangeStartDate").getValue(),
                        input.get("BEstatementRangeEndDate").getValue());
                break;
            case "requestAccountPersonalStatement": // Выписка по счёту (выписка документом)
                output = StatementService.requestAccountPersonalStatement(dialogData,
                        input.get("BEaccountPersonalStatementName").getValue(),
                        input.get("BEstatementRangeStartDate").getValue(),
                        input.get("BEstatementRangeEndDate").getValue(),
                        input.get("BEaccountPersonalStatementFileFormat").getValue());
                break;
            // Валидация отчётной даты для заказа справки для госслужащих через Task Tracker
            case "validationCivilServantDateStatus":
                output = StatementService.validationCivilServantDateStatus(
                        input.get("BEcivilServantDate").getValue());
                break;
            // Создание заявки в TaskTracker по заказу справки для госслужащих
            case "claimsIssueCivilServant":
                output = claimsIssueCivilServant(input.get("BEcivilServantDate").getValue(), dialogData);
                break;
            case "requestCivilServantsPersonalStatement": // Выписка для государственных служащих
                output = StatementService.requestCivilServantsPersonalStatement(
                        input.get("BEcivilServantDate").getValue(), dialogData);
                break;
            case "getEmployeeLogin":
                output = getEmployeeLogin(dialogData);
                break;
            // Создание заявки в TaskTracker по закрытию КК с аннулированным лимитом
            case "claimsIssueCardCreditClose":
                output = claimsIssueCardCreditClose(input.get("isCardCreditClosing").getValue(), dialogData);
                break;
            case "findCardsCreditFinance":
                output = findCardsCreditFinance(dialogData);
                break;
            case "claimsIssueCardOverdraftClose":
                output = claimsIssueCardOverdraftClose(input.get("BEcloseAccount").getValue(), dialogData);
                break;
            default:
                break;
        }
        return output;
    }

    @Override
    public void applyInference(String function, Map<String, ApiField> input, Map<String, ApiField> output) {
        switch (function) {
            case "defineOriginalIntent":
                this.dialogData.checkIntentInHistory(output);
                break;
            case "getIntent":
                if (!dialogData.getFieldToElicit().isPresent()) {
                    dialogLog(dialogData, "На данном шаге не запрашивается никакое поле - пропускаем NLU");
                    break;
                }
                if (!dialogData.getUserUtterance().isPresent()) {
                    dialogLog(dialogData, "Нет клиентской фразы - пропускаем NLU");
                    break;
                }
                try {
                    Utterance utterance = new Utterance(dialogData.getUserUtterance().get(),
                            dialogData.getFieldToElicit().get(), dialogData.getTargetName(),
                            dialogData.getLastActionName(), dialogData);
                    utterance.runNlu(output);
                } catch (SQLException e) {
                    dialogLogWarn(dialogData, "Ошибка при работе NLU: " + e);
                }
                break;
        }
    }

    @Override
    public boolean onDialogClose() {
        Map<String, Field> fields = dialogData.fieldsContainer.getAllFields();

        // Сохранение результата обзвона Outbound
        Outbound.setContactStatus(fields.getOrDefault("OutboundSessionId", null),
                fields.getOrDefault("OutboundContactId", null),
                fields.getOrDefault("OutboundPhoneResult", null),
                fields.getOrDefault("OutboundBusinessResult", null),
                fields.getOrDefault("OutboundRescheduleMinutes", null));

        // Сохранение результатов тестов Edu_Bot
        try {
            SqlRequest.saveEduResults(fields.get("BEomniCustomerId").getFieldInstanceValue(),
                    fields.get("checkpointIisEntryTestResult").getFieldInstanceValue(),
                    fields.get("checkpointIisFinTestResult").getFieldInstanceValue(),
                    fields.get("checkpointIisViktorinaResult").getFieldInstanceValue(),
                    fields.get("checkpointIppEntryTestResult").getFieldInstanceValue(),
                    fields.get("checkpointIppFinTestResult").getFieldInstanceValue(),
                    fields.get("checkpointIppViktorinaResult").getFieldInstanceValue(),
                    fields.get("checkpointMozhnoEntryTestResult").getFieldInstanceValue(),
                    fields.get("checkpointMozhnoFinTestResult").getFieldInstanceValue(),
                    fields.get("checkpointMozhnoViktorinaResult").getFieldInstanceValue(),
                    fields.get("checkpointPifEntryTestResult").getFieldInstanceValue(),
                    fields.get("checkpointPifFinTestResult").getFieldInstanceValue(),
                    fields.get("checkpointPifViktorinaResult").getFieldInstanceValue(),
                    fields.get("checkpointPremEntryTestResult").getFieldInstanceValue(),
                    fields.get("checkpointPremFinTestResult").getFieldInstanceValue(),
                    fields.get("checkpointPremViktorinaResult").getFieldInstanceValue());
        } catch (Exception ignored) {
        }

        // Отправка SMS
        if (dialogData.getFieldValue("BEsmsText") != null) {
            String phone = dialogData.fieldsContainer.getAniField().getFieldInstanceValue();
            String text = dialogData.getFieldValue("BEsmsText");
            Mfm.sendCustomMessage(phone, text, dialogData);
        }

        return true;
    }

    public DataPooler getCopy() {
        return new BotModule();
    }
}
