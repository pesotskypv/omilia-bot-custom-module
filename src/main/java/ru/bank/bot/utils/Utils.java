package ru.bank.bot.utils;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.bot.Email;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Utils {
    public static String Stream2String(InputStream inputStream) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            String output = "";
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                output += line;
            }
            return output;
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    /**
     * Метод очищает телефон от любых знаков кроме цифр, и возвращает правые 10.
     */
    public static String clearPhone (String input) throws PhoneShorterThan10Exception {
        String digitsonly = input.replaceAll("[^\\d]", "");
        if (digitsonly.length()>=10) {
            return digitsonly.substring(digitsonly.length()-10); // Берём правые 10 цифр
        }
        else {
            throw new PhoneShorterThan10Exception(input);
        }
    }

    public static class PhoneShorterThan10Exception extends Exception {
        public PhoneShorterThan10Exception(String sourcePhone) {
            super("Телефон " + sourcePhone + " не содержит 10 цифр");
        }
    }

    public static void toDiamantLog (String row) {
        toDiamantLog(null, row, "debug");
    }

    public static void toDiamantLog (String row, Exception exception) {
        toDiamantLog(null, row, "error");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        toDiamantLog(sw.toString());
    }

    public static void toDiamantLog (DialogData dialogData, String row) {
        toDiamantLog(dialogData, row, "debug");
    }

    public static void toDiamantLog (DialogData dialogData, String row, Exception exception) {
        toDiamantLog(dialogData, row, "error");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        toDiamantLog(dialogData, sw.toString(), "error");
        Email.notifyAboutException(dialogData, row + "\n" + sw.toString());
    }

    /**
     * @param level Уровень логирования: info, green, warn, eror
     */
    public static void toDiamantLog (DialogData dialogData, String row, String level) {
        String subsystem = "CustomModule:> ";
        if (dialogData != null && dialogData.dlogger != null) {
            switch (level.toLowerCase()) {
                case "warning":
                    dialogData.dlogger.logWarning(subsystem + row);
                    break;
                case "info":
                    dialogData.dlogger.logInfo(subsystem + row);
                    break;
                case "green":
                    dialogData.dlogger.logGreen(subsystem + row);
                    break;
                case "error":
                    dialogData.dlogger.logError(subsystem + row);
                    break;
                default:
                    dialogData.dlogger.log(subsystem + row);
                    break;
            }
        } else if (CustomConfig.glogger != null) {
            switch (level.toLowerCase()) {
                case "warning":
                    CustomConfig.glogger.logWarning(row);
                    break;
                case "info":
                    CustomConfig.glogger.logInfo(row);
                    break;
                case "green":
                    CustomConfig.glogger.logGreen(row);
                    break;
                case "error":
                    CustomConfig.glogger.logError(row);
                    break;
                default:
                    CustomConfig.glogger.log(row);
                    break;
            }
        } else {
            System.out.println(row);
        }
    }

    public static String clearXmlSpecChars(String input) {
        return input.replaceAll("[#'&\"\\[\\]]", " ");
    }

    public static String outputMap2String (Map<String, ApiField> outputMap) {
        String output = "";
        for (ApiField apiField : outputMap.values()) {
            if (!output.equals("")) { output += "; "; }
            if (apiField.getStatus().equals(FieldStatus.DEFINED)) {
                output += apiField.getName() + " = " + apiField.getValue();
            } else if (apiField.getStatus().equals(FieldStatus.UNDEFINED)) {
                output += apiField.getName() + ".status = " + apiField.getStatus();
            } else {
                output += apiField.getName() + " = " + apiField.getValue() + " (" + apiField.getStatus() + ")";
            }
        }
        return output;
    }
}
