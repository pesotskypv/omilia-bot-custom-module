package ru.bank.util;

import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;

import java.util.Map;
import java.util.stream.IntStream;

public class Utils {
    static final String SUB_SYSTEM = "CustomModule:> ";

    public static void dialogLog(DialogData dialogData, String message) {
        dialogData.dlogger.log(SUB_SYSTEM + message);
    }

    public static void dialogLogInfo(DialogData dialogData, String message) {
        dialogData.dlogger.logInfo(SUB_SYSTEM + message);
    }

    public static void dialogLogGreen(DialogData dialogData, String message) {
        dialogData.dlogger.logGreen(SUB_SYSTEM + message);
    }

    public static void dialogLogWarn(DialogData dialogData, String message) {
        dialogData.dlogger.logWarning(SUB_SYSTEM + message);
    }

    public static void dialogLogErr(DialogData dialogData, String message) {
        dialogData.dlogger.logError(SUB_SYSTEM + message);
    }

    public static void genericLog(String message) {
        CustomConfig.glogger.log(SUB_SYSTEM + message);
    }

    public static void genericLogInfo(String message) {
        CustomConfig.glogger.logInfo(SUB_SYSTEM + message);
    }

    public static void genericLogGreen(String message) {
        CustomConfig.glogger.logGreen(SUB_SYSTEM + message);
    }

    public static void genericLogWarn(String message) {
        CustomConfig.glogger.logWarning(SUB_SYSTEM + message);
    }

    public static void genericLogErr(String message) {
        CustomConfig.glogger.logError(SUB_SYSTEM + message);
    }

    /**
     * Преобразование Map в следующий вид:
     * key1: "value1"
     * key2: "value2"
     */
    public static String mapToStringForLog(Map<String, String> inputMap) {
        StringBuilder output = new StringBuilder();
        inputMap.forEach((key, value) -> output.append(key).append(": \"").append(value).append("\"\n"));
        return output.toString();
    }

    /**
     * EXTERNAL_CURRENCY_TRANSFER -> externalCurrencyTransfer
     */
    public static String stringToCamelCase(String value) {
        final String sentence = value.toLowerCase();
        StringBuilder camel = new StringBuilder();

        IntStream.range(0, sentence.length()).forEach(i -> {
            if (i != 0 && sentence.charAt(i - 1) == '_') {
                camel.append(sentence.substring(i, i + 1).toUpperCase());
            } else {
                camel.append(sentence.charAt(i));
            }
        });
        return camel.toString().replace("_", "");
    }

    public static String phoneTo10Dig(String input) {
        String dig = input.replaceAll("[^\\d]", "");
        int pl = dig.length();

        if (pl >= 10) {
            return dig.substring(pl - 10);
        } else {
            return null;
        }
    }
}
