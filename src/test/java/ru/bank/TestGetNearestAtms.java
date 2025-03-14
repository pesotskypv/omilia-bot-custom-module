package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.doatm.Actions;
import ru.bank.bot.doatm.Servicepoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.bank.utilsForTests.printFields;

public class TestGetNearestAtms {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestGetNearestAtms() {
        properties.put("yandexapikey", "45dfde94-12dc-4a8f-ba29-555eff22b6d5");
        properties.put("BspApiAddr", "https://gateway-bsp-chat-prod.apps.ocp.bank.ru");
        CustomConfig.properties = properties;
    }

    @Test
    public void any_atm_belorusskaya() {
        inputFields.put("BEatmCity", ApiField.builder().value("москва").status(FieldStatus.DEFINED).build());
        inputFields.put("BEatmAddress", ApiField.builder().value("метро белорусская").status(FieldStatus.DEFINED)
                .build());
        inputFields.put("BEatmBankOrAny", ApiField.builder().value("any").status(FieldStatus.DEFINED).build());
        inputFields.put("BEatmService", ApiField.builder().value("getRUB").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getNearestAtms", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetNearestAtmsStatus").getValue());
        // ближайший адрес должен быть как минимум в Москва
        assertTrue(result.get("BEatmAddress1").getValue().toLowerCase().contains("москва"));
    }

    @Test
    public void bank_belorusskaya() {
        inputFields.put("BEatmCity", ApiField.builder().value("москва").status(FieldStatus.DEFINED).build());
        inputFields.put("BEatmAddress", ApiField.builder().value("метро белорусская").status(FieldStatus.DEFINED)
                .build());
        inputFields.put("BEatmBankOrAny", ApiField.builder().value("bank").status(FieldStatus.DEFINED).build());
        inputFields.put("BEatmService", ApiField.builder().value("getRUB").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getNearestAtms", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetNearestAtmsStatus").getValue());
    }

    @Test
    public void getServicepoints() throws Exception {
        Set<Servicepoint> atms  = Actions.getServicepoints(55.777393
                , 37.58221
                , Actions.SPType.OFFICE
                , Arrays.asList("mortgage")
                , 5);
        atms.forEach(x -> System.out.println(x.getClearAddress()));

    }


}