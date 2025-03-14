package ru.bank.bot.nlg;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.omilia.diamant.dialog.Locale;
import com.omilia.diamant.dialog.components.fields.FieldsContainer;
import com.omilia.diamant.dialog.components.fields.fieldrealizers.FieldRealizer;
import com.omilia.diamant.dialog.promptlibrary.PromptID;
import com.omilia.diamant.dialog.promptlibrary.PromptLibrary;
import com.omilia.diamant.loggers.DialogLogger;
import ru.bank.bot.HttpRequests;
import ru.bank.bot.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ru.bank.bot.doatm.Servicepoint.clearAddress;
import static ru.bank.bot.utils.Utils.clearXmlSpecChars;

public class AddressRealizer implements FieldRealizer {
    private static final Map<String, PromptID> registeredPromptIDs = new HashMap<>();

    public void registerPrompts(PromptLibrary promptLibrary, String realizerName) {
        // Тишина
        PromptID silencePrompt = promptLibrary.registerFlaggedPrompt(Locale.RU_RU, "-", "silence", false);
        registeredPromptIDs.put("-", silencePrompt);

//        String promptContent = "test prompt";
//        PromptID pid = promptLibrary.registerFlaggedPrompt(Locale.EN_US, promptContent, "test info", false);
//        registeredPromptIDs.put(promptContent, pid);

        // Банкоматы
        try {
            JsonElement response = HttpRequests.sendPostReturnJsonE(
                    "https://gateway-drbs-prod.apps.ocp.bank.rus/branch/v2/atm"
                    , "{ \"elemPerPage\": 0, \"page\": 0}", false);
            Set<Map.Entry<String, JsonElement>> entrySet = response.getAsJsonObject().get("data").getAsJsonObject()
                    .get("elements").getAsJsonObject().entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                JsonObject JOfields = entry.getValue().getAsJsonObject().get("fields").getAsJsonObject();
                // Формирование адреса
                JsonObject JOplacement = JOfields.get("placement").getAsJsonObject();
                String atmAddress = "";
                if (!JOplacement.get("fullAddress").isJsonNull()) {     // Если есть готовый полный адрес - берём его
                    atmAddress = JOplacement.get("fullAddress").getAsString();
                } else {       // Собираем адрес из частей, если готового нет.
                    if (!JOplacement.get("region").isJsonNull()) {
                        atmAddress += JOplacement.get("region").getAsString();
                    }
                    if (!JOplacement.get("cityName").isJsonNull()) {
                        atmAddress += ", " + JOplacement.get("cityName").getAsString();
                    }
                    if (!JOplacement.get("street").isJsonNull()) {
                        atmAddress += ", " + JOplacement.get("street").getAsString();
                    }
                    if (!JOplacement.get("house").isJsonNull()) {
                        atmAddress += ", " + JOplacement.get("house").getAsString();
                    }
                    if (!JOplacement.get("full").isJsonNull()) {
                        atmAddress += ", " + JOplacement.get("full").getAsString();
                    }
                }

                // Если dopPlacementInfo ещё не в адресе - добавляем.
                if (!JOplacement.get("dopPlacementInfo").isJsonNull()
                        && !atmAddress.endsWith(JOplacement.get("dopPlacementInfo").getAsString())) {
                    atmAddress += ", " + JOplacement.get("dopPlacementInfo").getAsString();
                }

                atmAddress = clearAddress(clearXmlSpecChars(atmAddress));
                PromptID addressPrompt = promptLibrary
                        .registerFlaggedPrompt(Locale.RU_RU, atmAddress, "DO ATM search", false);
                registeredPromptIDs.put(atmAddress, addressPrompt);
            }
        } catch (IOException e) {
            Utils.toDiamantLog("Не удалось получить адреса АТМ для формирования роликов", e);
        }

        // Допофисы
        try {
            JsonElement response = HttpRequests.sendPostReturnJsonE(
                    "https://gateway-drbs-prod.apps.ocp.bank.ru/branch/v2/office"
                    , "{ \"elemPerPage\": 0, \"page\": 0}", false);
            Set<Map.Entry<String, JsonElement>> entrySet = response.getAsJsonObject().get("data").getAsJsonObject()
                    .get("elements").getAsJsonObject().entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                if (!entry.getValue().getAsJsonObject()
                        .get("fields").getAsJsonObject()
                        .get("address").getAsJsonObject()
                        .get("full").isJsonNull()) {
                    String address = entry.getValue().getAsJsonObject().get("fields").getAsJsonObject().get("address")
                            .getAsJsonObject().get("full").getAsString();
                    address = clearAddress(clearXmlSpecChars(address));
                    PromptID addressPrompt = promptLibrary
                            .registerFlaggedPrompt(Locale.RU_RU, address, "DO ATM search", false);
                    registeredPromptIDs.put(address, addressPrompt);
                }
            }
        } catch (IOException e) {
            Utils.toDiamantLog("Не удалось получить адреса ДО для формирования списка роликов", e);
        }
    }

    public FieldRealizer getCopy() {
        return new AddressRealizer();
    }

    public PromptID realizeValue(String realizeType, String rawValue, FieldsContainer fieldsContainer,
                                 DialogLogger logger) {
        logger.log("BotModule.jar:> realizeType: " + realizeType + " rawValue: " + rawValue);
        PromptID pid = new PromptID();
        if (registeredPromptIDs.containsKey(clearXmlSpecChars(rawValue))) {
            pid.postAdd(registeredPromptIDs.get(clearXmlSpecChars(rawValue)), true);
        } else {
            pid.postAdd(registeredPromptIDs.get("-"), true);
        }

        return pid;
    }

    public String realizeValueToString(String rawValue, FieldsContainer fieldList, DialogLogger logger) {
        return null;
    }
}
