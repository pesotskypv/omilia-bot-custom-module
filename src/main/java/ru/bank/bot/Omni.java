package ru.bank.bot;

import com.google.gson.JsonElement;
import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import ru.bank.bot.utils.OutputMap;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static ru.bank.bot.utils.Utils.toDiamantLog;

public class Omni {
    public static Map<String, ApiField> getQueueLength(String activity_id, String channel_identifier, String intent,
                                                       String BEqueueThresholdMedium, String BEqueueThresholdHigh) {
        Map<String, ApiField> output = new HashMap<>();
        String omniAddr = (String) CustomConfig.properties.get("omniAddr") + "/api/queue/length";
        String url = omniAddr + "?activity_id=" + activity_id;
        if (channel_identifier != null) {
            url = url + "&channel_identifier=" + channel_identifier;
        }
        if (intent != null) {
            url = url + "&intent=" + intent;
        }

        int BEqueueLength;
        try {
            JsonElement response = HttpRequests.sendGetReturnJson(url, true);
            BEqueueLength = response.getAsJsonObject().get("queue_length").getAsInt();
            output.put("BEqueueLength", ApiField.builder().name("BEqueueLength").value(String.valueOf(BEqueueLength))
                    .status(FieldStatus.DEFINED).build());
        } catch (Exception e) {
            output.put("BEgetQueueLengthStatus", ApiField.builder().name("BEgetQueueLengthStatus").value("error")
                    .status(FieldStatus.DEFINED).build());
            return output;
        }

        if (BEqueueThresholdHigh != null && BEqueueLength >= Integer.parseInt(BEqueueThresholdHigh)) {
            output.put("BEqueueThreshold", ApiField.builder().name("BEqueueThreshold").value("high")
                    .status(FieldStatus.DEFINED).build());
        } else if (BEqueueThresholdMedium != null && BEqueueLength >= Integer.parseInt(BEqueueThresholdMedium)) {
            output.put("BEqueueThreshold", ApiField.builder().name("BEqueueThreshold").value("medium")
                    .status(FieldStatus.DEFINED).build());
        } else {
            output.put("BEqueueThreshold", ApiField.builder().name("BEqueueThreshold").value("low")
                    .status(FieldStatus.DEFINED).build());
        }

        if (output.size() == 2) {
            output.put("BEgetQueueLengthStatus", ApiField.builder().name("BEgetQueueLengthStatus").value("ok")
                    .status(FieldStatus.DEFINED).build());
        } else {
            output.put("BEgetQueueLengthStatus", ApiField.builder().name("BEgetQueueLengthStatus").value("error")
                    .status(FieldStatus.DEFINED).build());
        }
        return output;
    }

    public static Map<String, ApiField> getCustomerId(String omniActivityId) {
        Map<String, ApiField> output = new HashMap<>();

        String connectionUrl =
                "jdbc:postgresql://" + (String) CustomConfig.properties.get("omniDbHost") + ":5432/"
                        + (String) CustomConfig.properties.get("omniDbName");

        ResultSet resultSet = null;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {
        } // Регистрация драйвера

        try (Connection connection = DriverManager.getConnection(connectionUrl, (String) CustomConfig.properties
                .get("omniDbUser"), (String) CustomConfig.properties.get("omniDbPass"));
             Statement statement = connection.createStatement()) {

            String selectSql = "SELECT c.customer_id  FROM public.interactions_activities a "
                    + "left join public.interactions_cases c on a.case_id = c.id "
                    + "where a.id = '" + omniActivityId + "'";
            resultSet = statement.executeQuery(selectSql);
            if (resultSet.next()) {
                String customer_id = resultSet.getString("customer_id");
                output.put("BEomniCustomerId", ApiField.builder().name("BEomniCustomerId").value(customer_id)
                        .status(FieldStatus.DEFINED).build());
                output.put("BEgetOmniCustomerIdStatus", ApiField.builder().name("BEgetOmniCustomerIdStatus")
                        .value("ok").status(FieldStatus.DEFINED).build());
                return output;
            }
        } catch (SQLException e) {
            if (CustomConfig.glogger != null) CustomConfig.glogger.logInfo(e.toString());
            output.put("BEgetOmniCustomerIdStatus", ApiField.builder().name("BEgetOmniCustomerIdStatus")
                    .value(e.toString()).status(FieldStatus.DEFINED).build());
            return output;
        }

        if (output.isEmpty()) {
            output.put("BEgetOmniCustomerIdStatus", ApiField.builder().name("BEgetOmniCustomerIdStatus")
                    .value("Empty SQL result").status(FieldStatus.DEFINED).build());
        }
        return output;
    }

    public static void getRecentDialogs(DialogData dialogData) {
        String activityId = dialogData.getFieldValue("activityIdNow");
        if (activityId == null) {
            toDiamantLog(dialogData, "Количество недавних диалогов не запрашивалось - нет activityIdNow.",
                    "warning");
            return;
        }

        String connectionUrl = "jdbc:postgresql://" + (String) CustomConfig.properties.get("omniDbHost") + ":5432/"
                + (String) CustomConfig.properties.get("omniDbName");

        ResultSet resultSet = null;

        String BEgetRecentDialogsT1 = "1 minute";
        String BEgetRecentDialogsT2 = "15 minutes";
        String BEgetRecentDialogsT3 = "1 hour";
        String BEgetRecentDialogsT4 = "10 hours";
        try {
            BEgetRecentDialogsT1 = (String) CustomConfig.properties.get("intervalT1");
            BEgetRecentDialogsT2 = (String) CustomConfig.properties.get("intervalT2");
            BEgetRecentDialogsT3 = (String) CustomConfig.properties.get("intervalT3");
            BEgetRecentDialogsT4 = (String) CustomConfig.properties.get("intervalT4");
        } catch (Exception ignored) {
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {
        } // Регистрация драйвера

        try (Connection connection = DriverManager.getConnection(connectionUrl, (String) CustomConfig.properties
                .get("omniDbUser"), (String) CustomConfig.properties.get("omniDbPass"));
             Statement statement = connection.createStatement();) {

            String selectSql = "select SUM(cases.during_t1) as t1, SUM(cases.during_t2) as t2," +
                    "SUM(cases.during_t3) as t3, SUM(cases.during_t4) as t4 " + "from ( " + "SELECT  " + "case " +
                    "when all_cases.updated_at >= NOW() at time zone 'UTC' - INTERVAL '" + BEgetRecentDialogsT1 +
                    "' then 1 " + "else 0 " + "end as during_t1 " + ", case " +
                    "when all_cases.updated_at >= NOW() at time zone 'UTC' - INTERVAL '" + BEgetRecentDialogsT2 +
                    "' then 1 " + "else 0 " + "end as during_t2 " + ", case " +
                    "when all_cases.updated_at >= NOW() at time zone 'UTC' - INTERVAL '" + BEgetRecentDialogsT3 +
                    "' then 1 " + "else 0 " + "end as during_t3 " + ", case  " +
                    "when all_cases.updated_at >= NOW() at time zone 'UTC' - INTERVAL '" + BEgetRecentDialogsT4 +
                    "' then 1 " + "else 0 " + "end as during_t4 " + "FROM public.interactions_activities src_activity "
                    + "left join public.interactions_cases src_case on src_activity.case_id = src_case.id " +
                    "left join public.interactions_cases all_cases on src_case.customer_id = all_cases.customer_id " +
                    "and all_cases.inserted_at > NOW() - INTERVAL '" + BEgetRecentDialogsT4 + "' " +
                    "and all_cases.id != src_case.id " + "where src_activity.id = '" + activityId + "' " + ") as cases";
            resultSet = statement.executeQuery(selectSql);
            if (resultSet.next()) {
                dialogData.setFieldValue("ICRclientsDialogsDuringT1", resultSet.getString("t1"));
                dialogData.setFieldValue("ICRclientsDialogsDuringT2", resultSet.getString("t2"));
                dialogData.setFieldValue("ICRclientsDialogsDuringT3", resultSet.getString("t3"));
                dialogData.setFieldValue("ICRclientsDialogsDuringT4", resultSet.getString("t4"));
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Получить статус персонального менеджера
     *
     * @param dialogData Для логирования
     */
    public static Map<String, ApiField> getPmStatus(String requestType, DialogData dialogData) {
        OutputMap output = new OutputMap();

        if (requestType == null
                || requestType.equalsIgnoreCase("undefined")
                || !requestType.matches("\\d+")) {
            toDiamantLog(dialogData, "Не удалось определить статус ПМ - некорректный или отсутствует RequestType.",
                    "warning");
            output.add("BEgetPmStatus", "error");
            return output.get();
        }

        String connectionUrl =
                "jdbc:postgresql://" + (String) CustomConfig.properties.get("omniDbHost") + ":5432/"
                        + (String) CustomConfig.properties.get("omniDbName");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {
        } // Регистрация драйвера

        try (Connection connection = DriverManager.getConnection(
                connectionUrl
                , (String) CustomConfig.properties.get("omniDbUser")
                , (String) CustomConfig.properties.get("omniDbPass"));
             Statement statement = connection.createStatement();) {

            String selectSql = "SELECT usr.logged, usr.active, usr.ready" + "\n" +
                    "from intents_request_types rt" + "\n" +
                    "join users_users usr on rt.description like 'login%' and rt.description = usr.name" + "\n" +
                    "WHERE rt.id = '" + requestType + "'";
            ResultSet resultSet = statement.executeQuery(selectSql);
            if (resultSet.next()) {
                output.add("BEpmActive", String.valueOf(resultSet.getBoolean("active")));
                output.add("BEpmLogged", String.valueOf(resultSet.getBoolean("logged")));
                output.add("BEpmReady", String.valueOf(resultSet.getBoolean("ready")));
                output.add("BEgetPmStatus", "ok");
            }
        } catch (Exception e) {
            toDiamantLog(dialogData, "Ошибка при выполнении запроса к БД Омни.", e);
        }

        if (output.get().size() == 0) {
            output.add("BEgetPmStatus", "error");
        }
        return output.get();
    }

}
