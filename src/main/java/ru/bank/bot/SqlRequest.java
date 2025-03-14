package ru.bank.bot;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import ru.bank.bot.nlu.*;
import ru.bank.bot.utils.Utils;

import java.sql.*;
import java.util.*;

import static ru.bank.bot.utils.Utils.clearPhone;
import static ru.bank.bot.utils.Utils.toDiamantLog;

public class SqlRequest {
    public static Map<String, ApiField> getAnnounces(String dtName, String channelID) {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        ResultSet resultSet = null;
        Map<String, ApiField> output = new HashMap<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        } // Регистрация драйвера
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement();) {

            String table = (String) CustomConfig.properties.get("getAnnouncesTable");
            String selectSql = "SELECT * from " + table + " WHERE dtName = '" + dtName + "' and channelID = '"
                    + channelID + "' ORDER BY announceID";
            resultSet = statement.executeQuery(selectSql); // Пробуем получить анонсы по указанному channelID

            while (resultSet.next()) {
                output.put("announce" + resultSet.getString("announceID"), ApiField.builder()
                        .name("announce" + resultSet.getString("announceID")).value(resultSet
                                .getString("announceText")).status(FieldStatus.DEFINED).build());
            }

            // Если нет анонсов по указанному channelID, пробуем default
            if (output.isEmpty()) {
                selectSql = "SELECT * from " + table + " WHERE dtName = '" + dtName
                        + "' and channelID = 'default' ORDER BY announceID";
                resultSet = statement.executeQuery(selectSql);
                while (resultSet.next()) {
                    output.put("announce" + resultSet.getString("announceID"), ApiField.builder()
                            .name("announce" + resultSet.getString("announceID")).value(resultSet
                                    .getString("announceText")).status(FieldStatus.DEFINED).build());
                }
            }

            if (output.isEmpty()) {
                output.put("announcementsNumber", ApiField.builder().name("announcementsNumber").value(String
                        .valueOf(output.size())).status(FieldStatus.DEFINED).build());
                output.put("BEgetAnnouncesStatus", ApiField.builder().name("BEgetAnnouncesStatus")
                        .value("Empty SQL result").status(FieldStatus.DEFINED).build());
                output.put("hasError", ApiField.builder().name("hasError").value("true").status(FieldStatus.DEFINED)
                        .build());

            } else {
                output.put("announcementsNumber", ApiField.builder().name("announcementsNumber")
                        .value(String.valueOf(output.size())).status(FieldStatus.DEFINED).build());
                output.put("BEgetAnnouncesStatus", ApiField.builder().name("BEgetAnnouncesStatus")
                        .value("ok").status(FieldStatus.DEFINED).build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            output.put("BEgetAnnouncesStatus", ApiField.builder().name("BEgetAnnouncesStatus").value(e.toString())
                    .status(FieldStatus.DEFINED).build());
            output.put("hasError", ApiField.builder().name("hasError").value("true").status(FieldStatus.DEFINED)
                    .build());
        }
        return output;
    }

    public static Map<String, ApiField> getReactions(String eventName, String outputFieldName) {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";
        ResultSet resultSet = null;
        Map<String, ApiField> output = new HashMap<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        } // Регистрация драйвера
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement();) {

            String table = (String) CustomConfig.properties.get("getReactionsTable");
            String selectSql = "SELECT * from " + table + " WHERE eventName = '" + eventName + "' ORDER BY reactionID";
            resultSet = statement.executeQuery(selectSql); // Пробуем получить анонсы по указанному channelID

            while (resultSet.next()) {
                output.put(outputFieldName + resultSet.getString("reactionID"), ApiField.builder()
                        .name(outputFieldName + resultSet.getString("reactionID")).value(resultSet
                                .getString("reactionText")).status(FieldStatus.DEFINED).build());
            }

            if (output.isEmpty()) {
                output.put("reactionsNumber", ApiField.builder().name("reactionsNumber")
                        .value(String.valueOf(output.size())).status(FieldStatus.DEFINED).build());
                output.put("BEgetReactionsStatus", ApiField.builder().name("BEgetReactionsStatus")
                        .value("Empty SQL result").status(FieldStatus.DEFINED).build());
                output.put("hasError", ApiField.builder().name("hasError").value("true").status(FieldStatus.DEFINED)
                        .build());
            } else {
                output.put("reactionsNumber", ApiField.builder().name("reactionsNumber")
                        .value(String.valueOf(output.size())).status(FieldStatus.DEFINED).build());
                output.put("BEgetReactionsStatus", ApiField.builder().name("BEgetReactionsStatus")
                        .value("ok").status(FieldStatus.DEFINED).build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            output.put("BEgetReactionsStatus", ApiField.builder().name("BEgetReactionsStatus").value(e.toString())
                    .status(FieldStatus.DEFINED).build());
            output.put("hasError", ApiField.builder().name("hasError").value("true")
                    .status(FieldStatus.DEFINED).build());
        }
        return output;
    }

    public static Map<String, ApiField> getRequests(String dtName, String channelID) {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";
        ResultSet resultSet = null;
        Map<String, ApiField> output = new HashMap<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        } // Регистрация драйвера
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement();) {

            String table = (String) CustomConfig.properties.get("getRequestsTable");
            String selectSql = "SELECT * from " + table + " WHERE dtName = '" + dtName + "' and channelID = '"
                    + channelID + "' ORDER BY requestID";
            resultSet = statement.executeQuery(selectSql); // Пробуем получить анонсы по указанному channelID

            while (resultSet.next()) {
                output.put("requestNormal" + resultSet.getString("requestID"), ApiField.builder()
                        .name("requestNormal" + resultSet.getString("requestID")).value(resultSet
                                .getString("requestTextNormal")).status(FieldStatus.DEFINED).build());
                output.put("requestError" + resultSet.getString("requestID"), ApiField.builder()
                        .name("requestError" + resultSet.getString("requestID")).value(resultSet
                                .getString("requestTextError")).status(FieldStatus.DEFINED).build());
                output.put("requestUnrecovered" + resultSet.getString("requestID"), ApiField.builder()
                        .name("requestUnrecovered" + resultSet.getString("requestID")).value(resultSet
                                .getString("requestTextUnrecovered")).status(FieldStatus.DEFINED).build());
            }

            // Если нет анонсов по указанному channelID, пробуем default
            if (output.isEmpty()) {
                selectSql = "SELECT * from " + table + " WHERE dtName = '" + dtName
                        + "' and channelID = 'default' ORDER BY requestID";
                resultSet = statement.executeQuery(selectSql);
                while (resultSet.next()) {
                    output.put("requestNormal" + resultSet.getString("requestID"), ApiField.builder()
                            .name("requestNormal" + resultSet.getString("requestID")).value(resultSet
                                    .getString("requestTextNormal")).status(FieldStatus.DEFINED).build());
                    output.put("requestError" + resultSet.getString("requestID"), ApiField.builder()
                            .name("requestError" + resultSet.getString("requestID")).value(resultSet
                                    .getString("requestTextError")).status(FieldStatus.DEFINED).build());
                    output.put("requestUnrecovered" + resultSet.getString("requestID"), ApiField.builder()
                            .name("requestUnrecovered" + resultSet.getString("requestID")).value(resultSet
                                    .getString("requestTextUnrecovered")).status(FieldStatus.DEFINED)
                            .build());
                }
            }

            if (output.isEmpty()) {
                output.put("requestsNumber", ApiField.builder().name("requestsNumber").value(String.valueOf(output
                        .size())).status(FieldStatus.DEFINED).build());
                output.put("BEgetRequestsStatus", ApiField.builder().name("BEgetRequestsStatus")
                        .value("Empty SQL result").status(FieldStatus.DEFINED).build());
                output.put("hasError", ApiField.builder().name("hasError").value("true").status(FieldStatus.DEFINED)
                        .build());

            } else {
                output.put("requestsNumber", ApiField.builder().name("requestsNumber").value(String.valueOf(output
                        .size())).status(FieldStatus.DEFINED).build());
                output.put("BEgetRequestsStatus", ApiField.builder().name("BEgetRequestsStatus").value("ok")
                        .status(FieldStatus.DEFINED).build());
            }
        } catch (SQLException e) {
            output.put("BEgetRequestsStatus", ApiField.builder().name("BEgetRequestsStatus").value(e.toString())
                    .status(FieldStatus.DEFINED).build());
            output.put("hasError", ApiField.builder().name("hasError").value("true").status(FieldStatus.DEFINED)
                    .build());
        }
        return output;
    }

    public static Map<String, ApiField> getConfirms(String dtName, String channelID) {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        ResultSet resultSet = null;
        Map<String, ApiField> output = new HashMap<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        } // Регистрация драйвера
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement();) {

            String table = (String) CustomConfig.properties.get("getConfirmsTable");
            String selectSql = "SELECT * from " + table + " WHERE dtName = '" + dtName + "' and channelID = '"
                    + channelID + "' ORDER BY confirmID";
            resultSet = statement.executeQuery(selectSql); // Пробуем получить анонсы по указанному channelID

            while (resultSet.next()) {
                output.put("confirmNormal" + resultSet.getString("confirmID"), ApiField.builder()
                        .name("confirmNormal" + resultSet.getString("confirmID")).value(resultSet
                                .getString("confirmTextNormal")).status(FieldStatus.DEFINED).build());
                output.put("confirmError" + resultSet.getString("confirmID"), ApiField.builder()
                        .name("confirmError" + resultSet.getString("confirmID")).value(resultSet
                                .getString("confirmTextError")).status(FieldStatus.DEFINED).build());
                output.put("confirmUnrecovered" + resultSet.getString("confirmID"), ApiField.builder()
                        .name("confirmUnrecovered" + resultSet.getString("confirmID")).value(resultSet
                                .getString("confirmTextUnrecovered")).status(FieldStatus.DEFINED).build());
            }

            // Если нет анонсов по указанному channelID, пробуем default
            if (output.isEmpty()) {
                selectSql = "SELECT * from " + table + " WHERE dtName = '" + dtName
                        + "' and channelID = 'default' ORDER BY confirmID";
                resultSet = statement.executeQuery(selectSql);
                while (resultSet.next()) {
                    output.put("confirmNormal" + resultSet.getString("confirmID"), ApiField.builder()
                            .name("confirmNormal" + resultSet.getString("confirmID")).value(resultSet
                                    .getString("confirmTextNormal")).status(FieldStatus.DEFINED).build());
                    output.put("confirmError" + resultSet.getString("confirmID"), ApiField.builder()
                            .name("confirmError" + resultSet.getString("confirmID")).value(resultSet
                                    .getString("confirmTextError")).status(FieldStatus.DEFINED).build());
                    output.put("confirmUnrecovered" + resultSet.getString("confirmID"), ApiField.builder()
                            .name("confirmUnrecovered" + resultSet.getString("confirmID")).value(resultSet
                                    .getString("confirmTextUnrecovered")).status(FieldStatus.DEFINED)
                            .build());
                }
            }

            if (output.isEmpty()) {
                output.put("confirmsNumber", ApiField.builder().name("confirmsNumber").value(String.valueOf(output
                        .size())).status(FieldStatus.DEFINED).build());
                output.put("BEgetConfirmsStatus", ApiField.builder().name("BEgetConfirmsStatus")
                        .value("Empty SQL result").status(FieldStatus.DEFINED).build());
                output.put("hasError", ApiField.builder().name("hasError").value("true").status(FieldStatus.DEFINED)
                        .build());

            } else {
                output.put("confirmsNumber", ApiField.builder().name("confirmsNumber").value(String.valueOf(output
                        .size())).status(FieldStatus.DEFINED).build());
                output.put("BEgetConfirmsStatus", ApiField.builder().name("BEgetConfirmsStatus").value("ok")
                        .status(FieldStatus.DEFINED).build());
            }
        } catch (SQLException e) {
            output.put("BEgetConfirmsStatus", ApiField.builder().name("BEgetConfirmsStatus").value(e.toString())
                    .status(FieldStatus.DEFINED).build());
            output.put("hasError", ApiField.builder().name("hasError").value("true").status(FieldStatus.DEFINED)
                    .build());
        }
        return output;
    }

    public static Map<String, ApiField> saveEduUserData(String OmniCustomerId, String FirstName, String LastName,
                                                        String ValidEmail) {
        Map<String, ApiField> output = new HashMap<>();
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        } // Регистрация драйвера
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement();) {

            String sql1 = "SELECT * FROM edu.userdata WHERE OmniCustomerId = '" + OmniCustomerId + "'";
            ResultSet resultSet = statement.executeQuery(sql1);

            if (resultSet.next()) {
                String sql2 = "UPDATE edu.userdata SET FirstName = '" + FirstName + "', LastName = '" + LastName
                        + "', ValidEmail = '" + ValidEmail + "' "
                        + "WHERE OmniCustomerId=" + "'" + OmniCustomerId + "'";
                statement.execute(sql2);
            } else {
                String sql2 = "INSERT INTO edu.userdata (OmniCustomerId, FirstName, LastName, ValidEmail) VALUES ("
                        + "'" + OmniCustomerId + "', "
                        + "'" + FirstName + "', "
                        + "'" + LastName + "', "
                        + "'" + ValidEmail + "')";
                statement.execute(sql2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            output.put("BEsaveEduUserDataStatus", ApiField.builder().name("BEsaveEduUserDataStatus").value(e.toString())
                    .status(FieldStatus.DEFINED).build());
            return output;
        }

        output.put("BEsaveEduUserDataStatus", ApiField.builder().name("BEsaveEduUserDataStatus").value("ok")
                .status(FieldStatus.DEFINED).build());
        return output;
    }

    public static Map<String, ApiField> getEduData(String OmniCustomerId) {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        ResultSet resultSet = null;
        Map<String, ApiField> output = new HashMap<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        } // Регистрация драйвера
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement();) {
            String selectSql = "SELECT TOP 1 * FROM edu.userdata WHERE OmniCustomerId = '" + OmniCustomerId + "'";
            resultSet = statement.executeQuery(selectSql);
            if (resultSet.next()) {
                ResultSetMetaData rsMeta = resultSet.getMetaData();
                for (int i = 2; i <= rsMeta.getColumnCount(); i++) {
                    ApiField result = ApiField.builder().name(rsMeta.getColumnName(i)).value(resultSet.getString(i))
                            .status(FieldStatus.DEFINED).build();
                    output.put(rsMeta.getColumnName(i), result);
                }
            }
        } catch (SQLException e) {
            output.put("BEgetEduDataStatus", ApiField.builder().name("BEgetEduDataStatus").value(e.toString())
                    .status(FieldStatus.DEFINED).build());
            return output;
        }

        if (output.isEmpty()) {
            output.put("BEgetEduDataStatus", ApiField.builder().name("BEgetEduDataStatus").value("Empty SQL result")
                    .status(FieldStatus.DEFINED).build());
            return output;
        } else {
            output.put("BEgetEduDataStatus", ApiField.builder().name("BEgetEduDataStatus").value("ok")
                    .status(FieldStatus.DEFINED).build());
        }

        return output;
    }

    public static void saveEduResults(String BEomniCustomerId
            , String checkpointIisEntryTestResult
            , String checkpointIisFinTestResult
            , String checkpointIisViktorinaResult
            , String checkpointIppEntryTestResult
            , String checkpointIppFinTestResult
            , String checkpointIppViktorinaResult
            , String checkpointMozhnoEntryTestResult
            , String checkpointMozhnoFinTestResult
            , String checkpointMozhnoViktorinaResult
            , String checkpointPifEntryTestResult
            , String checkpointPifFinTestResult
            , String checkpointPifViktorinaResult
            , String checkpointPremEntryTestResult
            , String checkpointPremFinTestResult
            , String checkpointPremViktorinaResult) {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        } // Регистрация драйвера
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement();) {
            String sql1 = "UPDATE edu.userdata SET "
                    + "checkpointIisEntryTestResult = '" + checkpointIisEntryTestResult + "'"
                    + ", checkpointIisFinTestResult = '" + checkpointIisFinTestResult + "'"
                    + ", checkpointIisViktorinaResult = '" + checkpointIisViktorinaResult + "'"
                    + ", checkpointIppEntryTestResult = '" + checkpointIppEntryTestResult + "'"
                    + ", checkpointIppFinTestResult = '" + checkpointIppFinTestResult + "'"
                    + ", checkpointIppViktorinaResult = '" + checkpointIppViktorinaResult + "'"
                    + ", checkpointMozhnoEntryTestResult = '" + checkpointMozhnoEntryTestResult + "'"
                    + ", checkpointMozhnoFinTestResult = '" + checkpointMozhnoFinTestResult + "'"
                    + ", checkpointMozhnoViktorinaResult = '" + checkpointMozhnoViktorinaResult + "'"
                    + ", checkpointPifEntryTestResult = '" + checkpointPifEntryTestResult + "'"
                    + ", checkpointPifFinTestResult = '" + checkpointPifFinTestResult + "'"
                    + ", checkpointPifViktorinaResult = '" + checkpointPifViktorinaResult + "'"
                    + ", checkpointPremEntryTestResult = '" + checkpointPremEntryTestResult + "'"
                    + ", checkpointPremFinTestResult = '" + checkpointPremFinTestResult + "'"
                    + ", checkpointPremViktorinaResult = '" + checkpointPremViktorinaResult + "'"
                    + " WHERE OmniCustomerId=" + "'" + BEomniCustomerId + "'";
            statement.executeQuery(sql1);
        } catch (Exception ignored) {
        }

    }

    public static void getIcrDbLookup(DialogData dialogData) {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("OutboundDbHost") + ":1433"
                        + ";database=" + "DBLookup"
                        + ";user=" + (String) CustomConfig.properties.get("OutboundDbUser")
                        + ";password=" + (String) CustomConfig.properties.get("OutboundDbPass")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        // Получение siebelId или телефона
        String idOrPhone = dialogData.getFieldValue("siebelId");
        if (idOrPhone == null && dialogData.fieldsContainer.getAniField().isDefined()) {
            try {
                idOrPhone = clearPhone(dialogData.fieldsContainer.getAniField().getFieldInstanceValue());
            } catch (Utils.PhoneShorterThan10Exception e) {
                toDiamantLog(dialogData, "Поиск в DBLookup не выполнялся - нет siebelId и телефон не валиден:"
                        + e.getMessage(), "warning");
                return;
            }
        } else if (idOrPhone == null) {
            toDiamantLog(dialogData, "Поиск в DBLookup не выполнялся - нет siebelId и телефона");
            return;
        }

        // Получение канала
        String channel = dialogData.getFieldValue("User.id");
        if (channel == null) {
            toDiamantLog(dialogData, "Поиск ICR по DBLookup не выполнялся - нет id канала (User.id)");
            return;
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (Exception ignored) {
        } // Регистрация драйвера

        try (Connection connection = DriverManager.getConnection(connectionUrl);) {
            String sql = "EXEC dbo.ICRomilia ?,?"; // new
            PreparedStatement statement = connection.prepareStatement(sql); // new
            statement.setEscapeProcessing(true); // new
            statement.setQueryTimeout(5); // new
            statement.setString(1, idOrPhone);
            statement.setString(2, channel);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                ResultSetMetaData rsMeta = resultSet.getMetaData();
                for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                    String columnValue = resultSet.getString(i);
                    String columnName = rsMeta.getColumnName(i);
                    if (!resultSet.wasNull() && columnName.startsWith("ICR")) {
                        dialogData.setFieldValue(columnName, columnValue);
                    }
                }
            } else {
                toDiamantLog(dialogData, "В DBLookup ничего не найдено по id " + idOrPhone + " и каналу "
                        + channel);
            }
        } catch (SQLException e) {
            toDiamantLog(dialogData, "Ошибка при получении ICR из DBLookup: ", e);
        }
    }

    public static List<Entity> getDictionaryMatches(List<String> utteranceParts, DialogData dialogData)
            throws SQLException {
        String utterancePartsSql = "";
        for (String uttPart : utteranceParts) {
            utterancePartsSql += "N'" + uttPart + "', ";
        }
        utterancePartsSql = utterancePartsSql.replaceAll(", $", "");
        String selectSql = "SELECT d.*, e.Entity" + "\n" +
                "FROM nlu.Dictionaries d" + "\n" +
                "JOIN nlu.Entities e ON d.Dictionary = e.Link2Dictionary" + "\n" +
                "WHERE Token IN (" + utterancePartsSql + ")";

        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<Entity> entities = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (Exception ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(selectSql);
        ResultSetMetaData rsMeta = resultSet.getMetaData();

        while (resultSet.next()) {
            Entity entity = new Entity(resultSet.getString("Entity")
                    , resultSet.getString("Token"));
            for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                entity.addFeature(rsMeta.getColumnName(i), resultSet.getString(i));
            }
            entities.add(entity);
        }
        return entities;
    }

    /**
     * Запрос интентов, которые совпадают только по имени Entity
     */
    public static List<Intent> getIntentsCandidates(List<Entity> entities) throws SQLException {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<Intent> intents = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        String entitiesSql = "";
        for (Entity entity : entities) {
            entitiesSql += "'" + entity.name + "', ";
        }
        entitiesSql = entitiesSql.replaceAll(", $", "");

        String selectSql = "SELECT DISTINCT int_all_rules.*, IIF ( ambs.Intent IS NOT NULL , 1, 0 ) as isAmbiguous" + "\n" +
                "FROM nlu.Intents int_matched_rules" + "\n" +
                "LEFT JOIN nlu.Intents int_all_rules ON int_matched_rules.Intent = int_all_rules.Intent" + "\n" +
                "LEFT JOIN nlu.AmbiguitiesList ambs ON int_matched_rules.Intent = ambs.Intent" + "\n" +
                "WHERE int_matched_rules.Entity IN (" + entitiesSql + ")";
        ResultSet resultSet = statement.executeQuery(selectSql);
        ResultSetMetaData rsMeta = resultSet.getMetaData();
        List<String> columns = getColumns(rsMeta);

        while (resultSet.next()) {
            String rowIntentName = resultSet.getString("Intent");
            boolean isAmbigous = resultSet.getBoolean("isAmbiguous");
            Optional<Intent> existingIntent = intents.stream()
                    .filter(x -> x.intentName.equals(rowIntentName))
                    .findFirst();
            Constraint constraint = new Constraint(resultSet.getString("ConstraintSet")
                    , resultSet.getString("Entity")
                    , resultSet.getString("Operator"));

            // Наполняем Constraint условиями по Feature
            for /* Перебор столбцов */ (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                if /* Ищем стобцы "Feature..." */ (columns.contains("Feature" + String.valueOf(i))) {
                    constraint.addFeature(resultSet.getString("Feature" + String.valueOf(i))
                            , resultSet.getString("Value" + String.valueOf(i)));
                }
            }

            // Если интент существует - добавляем Constraint ему, если нет - создаём новый.
            if (existingIntent.isPresent()) {
                existingIntent.get().addConstraint(constraint);
            } else {
                Intent newIntent = new Intent(rowIntentName, isAmbigous);
                newIntent.addConstraint(constraint);
                intents.add(newIntent);
            }
        }

        return intents;
    }

    /**
     * Запрос контекстных правил
     */
    public static List<ContextRule> getAllContextRules() throws SQLException {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<ContextRule> output = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        String selectSql = "SELECT * from nlu.ContextRules ORDER BY [Order]";
        ResultSet resultSet = statement.executeQuery(selectSql);
        ResultSetMetaData rsMeta = resultSet.getMetaData();
        List<String> columns = getColumns(rsMeta);

        while (resultSet.next()) {
            ContextRule contextRule = new ContextRule(resultSet.getString("Name")
                    , resultSet.getString("ContextName"));
            for /* Перебор столбцов */ (int i = 0; i <= rsMeta.getColumnCount(); i++) {
                if (columns.contains("Entity" + String.valueOf(i))) {
                    String entityName = resultSet.getString("Entity" + String.valueOf(i));
                    boolean mandatory = resultSet.getBoolean("Entity" + String.valueOf(i) + "mandatory");
                    contextRule.addDesiredEntity(i, entityName, mandatory);
                }
            }
            output.add(contextRule);
        }

        return output;
    }

    /**
     * Запрос действий типа Mark для контекстных правил
     */
    public static List<CrAction> getAllMarkActions() throws SQLException {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<CrAction> output = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        String selectSql = "SELECT * from nlu.ContextRuleMarkAction";
        ResultSet resultSet = statement.executeQuery(selectSql);
        ResultSetMetaData rsMeta = resultSet.getMetaData();
        List<String> columns = getColumns(rsMeta);

        while (resultSet.next()) {
            String contextRuleName = resultSet.getString("ContextRuleName");
            CrMarkAction.MarkActionEntityBuilder entityBuilder = new CrMarkAction.MarkActionEntityBuilder(resultSet
                    .getString("Entity"));
            for /* Перебор столбцов */ (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                // Явные Feature
                if (columns.contains("Feature" + String.valueOf(i))) {
                    entityBuilder.addExplicitFeature(resultSet.getString("Feature" + String.valueOf(i))
                            , resultSet.getString("Value" + String.valueOf(i)));
                }
                // Динамические Feature
                if (columns.contains("Feature" + String.valueOf(i))
                        && columns.contains("SourceEntityIndex" + String.valueOf(i))
                        && columns.contains("SourceEntityFeature" + String.valueOf(i))) {
                    entityBuilder.addLinkedFeature(
                            resultSet.getString("Feature" + String.valueOf(i))
                            , resultSet.getInt("SourceEntityIndex" + String.valueOf(i))
                            , resultSet.getString("SourceEntityFeature" + String.valueOf(i)));
                }
            }

            CrMarkAction markAction = new CrMarkAction(contextRuleName, entityBuilder);
            String spansFromStr = resultSet.getString("SpansFrom");
            if (spansFromStr != null) {
                markAction.setSpansFrom(Integer.valueOf(spansFromStr));
            }
            String spansToStr = resultSet.getString("SpansTo");
            if (spansToStr != null) {
                markAction.setSpansTo(Integer.valueOf(spansToStr));
            }
            output.add(markAction);
        }

        return output;
    }

    /**
     * Запрос всех действий SetFeature Explicit для контекстных правил
     */
    public static List<CrAction> getAllSetFeatureExplicitActions() throws SQLException {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<CrAction> output = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        String selectSql = "SELECT * from nlu.ContextRuleSetFeatureExplicitAction";
        ResultSet resultSet = statement.executeQuery(selectSql);
        ResultSetMetaData rsMeta = resultSet.getMetaData();

        while (resultSet.next()) {
            output.add(new CrSetFeatureExplicitAction(
                    resultSet.getString("ContextRuleName")
                    , resultSet.getInt("EntityIndex")
                    , resultSet.getString("Feature")
                    , resultSet.getString("Value")));
        }

        return output;
    }

    /**
     * Запрос всех действий SetFeature Dynamic для контекстных правил
     */
    public static List<CrAction> getAllSetFeatureDynamicActions() throws SQLException {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<CrAction> output = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        String selectSql = "SELECT * from nlu.ContextRuleSetFeatureDynamicAction";
        ResultSet resultSet = statement.executeQuery(selectSql);
        ResultSetMetaData rsMeta = resultSet.getMetaData();

        while (resultSet.next()) {
            output.add(new CrSetFeatureDynamicAction(
                    resultSet.getString("ContextRuleName")
                    , resultSet.getInt("EntityIndex")
                    , resultSet.getString("Feature")
                    , resultSet.getInt("SourceEntityIndex")
                    , resultSet.getString("SourceFeature")));
        }

        return output;
    }

    /**
     * Запрос всех действий Unmark для контекстных правил
     */
    public static List<CrAction> getAllUnmarkActions() throws SQLException {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<CrAction> output = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        String selectSql = "SELECT * from nlu.ContextRuleUnmarkAction";
        ResultSet resultSet = statement.executeQuery(selectSql);
        ResultSetMetaData rsMeta = resultSet.getMetaData();

        while (resultSet.next()) {
            output.add(new CrUnmarkAction(
                    resultSet.getString("ContextRuleName")
                    , resultSet.getInt("EntityIndex")));
        }

        return output;
    }

    /**
     * Запрос всех действий Change Span для контекстных правил
     */
    public static List<CrAction> getAllChangeSpanActions() throws SQLException {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<CrAction> output = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        String selectSql = "SELECT * from nlu.ContextRuleChangeSpanAction";
        ResultSet resultSet = statement.executeQuery(selectSql);
        ResultSetMetaData rsMeta = resultSet.getMetaData();

        while (resultSet.next()) {
            output.add(new CrChangeSpanAction(
                    resultSet.getString("ContextRuleName")
                    , resultSet.getInt("EntityIndex")
                    , resultSet.getInt("NewBeginIndex")
                    , resultSet.getInt("NewEndIndex")
            ));
        }

        return output;
    }

    /**
     * Получить список названий столбцов из ResultSetMetaData
     */
    public static List<String> getColumns(ResultSetMetaData rsMeta) throws SQLException {
        List<String> output = new LinkedList<>();
        for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
            output.add(rsMeta.getColumnName(i));
        }
        return output;
    }

    /**
     * Получение Feature Conditions для контекстных правил
     */
    public static List<CrFeatureCondition> getAllCrFeatureConditions() throws SQLException {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<CrFeatureCondition> output = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        String selectSql = "SELECT * from nlu.ContextRuleFeatureConditions";
        ResultSet resultSet = statement.executeQuery(selectSql);
        ResultSetMetaData rsMeta = resultSet.getMetaData();
        List<String> columns = getColumns(rsMeta);

        while (resultSet.next()) {
            CrFeatureCondition crFeatureCondition = new CrFeatureCondition(
                    resultSet.getString("ContextRule")
                    , resultSet.getInt("EntityIndex")
                    , resultSet.getString("Feature")
                    , resultSet.getBoolean("Negate"));

            for /* Перебор столбцов */ (int i = 0; i <= rsMeta.getColumnCount(); i++) {
                if /* Ищем стобцы ValueX */ (columns.contains("Value" + String.valueOf(i))) {
                    crFeatureCondition.addValue(resultSet.getString("Value" + String.valueOf(i)));
                }
            }

            output.add(crFeatureCondition);
        }

        return output;
    }

    /**
     * Получение Regex Conditions для контекстных правил
     */
    public static List<CrRegexCondition> getAllCrRegexConditions() throws SQLException {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<CrRegexCondition> output = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        String selectSql = "SELECT * from nlu.ContextRuleRegexConditions";
        ResultSet resultSet = statement.executeQuery(selectSql);
        ResultSetMetaData rsMeta = resultSet.getMetaData();
        List<String> columns = getColumns(rsMeta);

        while (resultSet.next()) {
            output.add(new CrRegexCondition(
                    resultSet.getString("ContextRule")
                    , resultSet.getInt("EntityIndex")
                    , resultSet.getString("Regex")
                    , resultSet.getBoolean("Negate")));
        }

        return output;
    }

    /**
     * Получение Entity Conditions для контекстных правил
     */
    public static List<CrEntityCondition> getAllCrEntityConditions() throws SQLException {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<CrEntityCondition> output = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        String selectSql = "SELECT * from nlu.ContextRuleEntityConditions";
        ResultSet resultSet = statement.executeQuery(selectSql);
        ResultSetMetaData rsMeta = resultSet.getMetaData();
        List<String> columns = getColumns(rsMeta);

        while (resultSet.next()) {
            output.add(new CrEntityCondition(
                    resultSet.getString("ContextRule")
                    , resultSet.getInt("EntityIndex")
                    , resultSet.getString("Operator")
                    , resultSet.getString("EntityName")
                    , resultSet.getBoolean("Negate")));
        }

        return output;
    }

    /**
     * Получение имён контекстов из БД
     */
    public static List<String> getContexts(String fieldToElicit, String targetName, String actionName)
            throws SQLException {
        String connectionUrl =
                "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                        + ";database=" + (String) CustomConfig.properties.get("dbname")
                        + ";user=" + (String) CustomConfig.properties.get("dbuser")
                        + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                        + ";encrypt=true"
                        + ";trustServerCertificate=true"
                        + ";loginTimeout=30";

        List<String> output = new LinkedList<>();

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
        }
        Connection connection = DriverManager.getConnection(connectionUrl);
        Statement statement = connection.createStatement();

        String selectSql = "SELECT * from nlu.Contexts";
        if (fieldToElicit != null && targetName != null && actionName != null) {
            selectSql += " WHERE FieldToElicit = '" + fieldToElicit + "' OR " +
                    " TargetName='" + targetName + "' OR " +
                    " ActionName='" + actionName + "'";
        }

        ResultSet resultSet = statement.executeQuery(selectSql);

        while (resultSet.next()) {
            String dbFieldToElicit = resultSet.getString("FieldToElicit");
            String dbTargetName = resultSet.getString("TargetName");
            String dbActionName = resultSet.getString("ActionName");

            if (
                    (dbFieldToElicit == null || dbFieldToElicit.equals(fieldToElicit))
                            && (dbTargetName == null || dbTargetName.equals(targetName))
                            && (dbActionName == null || dbActionName.equals(actionName))
            ) {
                output.add(resultSet.getString("Name"));
            }
        }

        return output;
    }

    /**
     * Сохранение нераспознанных слов для последующего анализа
     */
    public static void saveUnrecognized(String Token) {
        if (Token.length() > 64) {
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                String connectionUrl =
                        "jdbc:sqlserver://" + (String) CustomConfig.properties.get("dbserver") + ":1433"
                                + ";database=" + (String) CustomConfig.properties.get("dbname")
                                + ";user=" + (String) CustomConfig.properties.get("dbuser")
                                + ";password=" + (String) CustomConfig.properties.get("dbpassword")
                                + ";encrypt=true"
                                + ";trustServerCertificate=true"
                                + ";loginTimeout=3";
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                Connection connection = DriverManager.getConnection(connectionUrl);
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(3);
                String selectSql = "INSERT INTO nlu.Unrecognized ([Token]) VALUES ('" + Token + "')";
                statement.execute(selectSql);
            } catch (Exception ignored) {
            }
        });
        thread.start();
    }


}