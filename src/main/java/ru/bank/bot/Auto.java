package ru.bank.bot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;

import java.util.HashMap;
import java.util.Map;

public class Auto {
    public static Map<String, ApiField> sendLead(String firstName, String lastName, String patronymic, String birthday,
                                                 String phoneMobile, String email, String utmMedium) {
        Map<String, ApiField> output = new HashMap<>();
        phoneMobile = phoneMobile.replaceAll("[^\\d]", ""); // Очистка телефона
        if (phoneMobile.length() >= 10) {
            phoneMobile = phoneMobile.substring(phoneMobile.length() - 10); // Берём правые 10 цифр
        } else {
            output.put("BEsendAutoLeadStatus", ApiField.builder().name("BEsendLeadAutoStatus")
                    .value("Номер короче 10 цифр").status(FieldStatus.DEFINED).build());
            return output;
        }

        JsonObject rootObject = new JsonObject();
        rootObject.addProperty("firstName", firstName);
        rootObject.addProperty("lastName", lastName);
        rootObject.addProperty("patronymic", patronymic);
        rootObject.addProperty("birthday", birthday);
        rootObject.addProperty("phoneMobile", phoneMobile);
        rootObject.addProperty("originCode", "61bB679F4q");
        rootObject.addProperty("email", email);
        rootObject.addProperty("conditionPassed", true);
        JsonObject childObject = new JsonObject();
        rootObject.add("visitSource", childObject);
        childObject.addProperty("utmSource", "bank_chat");
        childObject.addProperty("utmMedium", utmMedium);
        childObject.addProperty("utmCampaign", "bank_chat_2020");

        String json = (new Gson()).toJson(rootObject);

        JsonElement response = HttpRequests
                .sendPostReturnJson("https://online-auto.bank.ru/OnlineApproval/api/user/user", json, false);

        if (response.toString().contains("account_id")) {
            output.put("BEsendLeadAutoStatus", ApiField.builder().name("BEsendLeadAutoStatus").value("ok")
                    .status(FieldStatus.DEFINED).build());
        } else {
            output.put("BEsendLeadAutoStatus", ApiField.builder().name("BEsendLeadAutoStatus")
                    .value(response.getAsJsonObject().get("error").getAsString()).status(FieldStatus.DEFINED).build());
        }
        return output;
    }
}
