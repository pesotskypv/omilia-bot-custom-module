package ru.bank.aid.service;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.bot.utils.OutputMap;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static ru.bank.util.Utils.*;

public class CcReportService {
    /**
     * BackEndCall getEmployeeLogin Получение Login сотрудника
     */
    public static Map<String, ApiField> getEmployeeLogin(DialogData dialogData) {
        genericLogInfo("Выполняется getEmployeeLogin");
        String url = "jdbc:sqlserver://" + CustomConfig.properties.get("ccReportDbHost") + ":1433"
                + ";database=" + CustomConfig.properties.get("ccReportDbName")
                + ";user=" + CustomConfig.properties.get("ccReportDbUser")
                + ";password=" + CustomConfig.properties.get("ccReportDbPass")
                + ";encrypt=true" + ";trustServerCertificate=true" + ";loginTimeout=30";
        String script = "SELECT DISTINCT TOP 1 employee_login FROM ContactCenter.ODPP.Clients_employee " +
                "WHERE cast(getdate() as date) <= dt_dismissal AND sbl_client_id = '" + dialogData.siebelId + "'";
        OutputMap output = new OutputMap();

        try (ResultSet rs = DriverManager.getConnection(url).prepareStatement(script).executeQuery()) {
            while (rs.next()) {
                output.add("BEgetEmployeeLogin", rs.getString("employee_login"));
            }
        } catch (SQLException e) {
            genericLogWarn("Ошибка при выполнении SQL-запроса: " + e);
            output.add("BEgetEmployeeLoginStatus", "error");
            return output.get();
        }
        if (output.get().size() > 0) {
            output.add("BEgetEmployeeLoginStatus", "ok");
        } else {
            output.add("BEgetEmployeeLoginStatus", "notFound");
        }
        return output.get();
    }

    /**
     * BackEndCall getCcReport Получение отчётов для чат-бота отчётности bReportBot
     */
    public static Map<String, ApiField> getCcReport(String table, DialogData dialogData) {
        genericLogInfo("Выполняется getCcReport");
        String url = "jdbc:sqlserver://" + CustomConfig.properties.get("ccReportDbHost") + ":1433"
                        + ";database=" + CustomConfig.properties.get("ccReportDbName")
                        + ";user=" + CustomConfig.properties.get("ccReportDbUser")
                        + ";password=" + CustomConfig.properties.get("ccReportDbPass")
                        + ";encrypt=true" + ";trustServerCertificate=true" + ";loginTimeout=30";
        ResultSet resultSet;
        Map<String, ApiField> output = new HashMap<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
            // Регистрация драйвера
        }

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            String selectSql = "SELECT * FROM " + table;
            resultSet = statement.executeQuery(selectSql);
            ResultSetMetaData rsMeta = resultSet.getMetaData();

            dialogLog(dialogData, "Таблица: " + table);
            while (resultSet.next()) {
                String rowname = resultSet.getString(1);
                for (int i = 2; i <= rsMeta.getColumnCount(); i++ ) {
                    String fldName = ("BE" + table + rowname
                            + rsMeta.getColumnName(i)).replaceAll("[^a-zA-Zа-яА-Я0-9]", "");
                    String fldValue = resultSet.getString(i);
                    // Первая проверка на null, чтобы не падали остальные проверки.
                    if (resultSet.wasNull()) {
                        fldValue = "null";
                        ApiField fldResult = ApiField.builder().name(fldName).value(fldValue)
                                .status(FieldStatus.DEFINED).build();
                        output.put(fldName, fldResult);
                        dialogLog(dialogData, fldName + ": '" + fldValue + "'");
                        continue;
                    }
                    // Поля, которые преобразовать в %
                    if (rsMeta.getColumnName(i).equals("Prog_Prog") || rsMeta.getColumnName(i).equals("SL_1")
                            || rsMeta.getColumnName(i).equals("SL2h") || rsMeta.getColumnName(i).equals("SL_20")
                            || rsMeta.getColumnName(i).equals("SL20") || rsMeta.getColumnName(i).equals("SL30")
                            || rsMeta.getColumnName(i).equals("SL_60") || rsMeta.getColumnName(i).equals("Occupancy")
                            || rsMeta.getColumnName(i).equals("Utilization")
                            || rsMeta.getColumnName(i).equals("Aban_rate")
                            || rsMeta.getColumnName(i).equals("Первая_попытка15м")) {
                        try {
                            fldValue = Math.round(resultSet.getDouble(i) * 100) + "%";
                        } catch (Exception ignored) {
                            // Если значение не округляется - оставляем как есть (присвоено в начале)
                        }
                    }
                    fldValue = fldValue.replaceAll("\\.0000000", "") // Отрезаем микросекунды
                            .replaceAll("^00:",""); // Отрезаем часы
                    ApiField fldResult = ApiField.builder().name(fldName).value(fldValue).status(FieldStatus.DEFINED)
                            .build();
                    output.put(fldName, fldResult);
                    dialogLog(dialogData, fldName + ": '" + fldValue + "'");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            output.put("BEgetCcReportStatus", ApiField.builder().name("BEgetCcReportStatus").value(e.toString())
                    .status(FieldStatus.DEFINED).build());
            return output;
        }

        if (output.isEmpty()) {
            output.put("BEgetCcReportStatus", ApiField.builder().name("BEgetCcReportStatus")
                    .value("Empty SQL result").status(FieldStatus.DEFINED).build());
        } else {
            output.put("BEgetCcReportStatus", ApiField.builder().name("BEgetCcReportStatus").value("ok")
                    .status(FieldStatus.DEFINED).build());
        }
        return output;
    }
}
