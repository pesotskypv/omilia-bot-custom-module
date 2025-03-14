package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.doatm.Office;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.bank.bot.doatm.Actions.getOfficeById;
import static ru.bank.utilsForTests.printFields;


public class TestGetCardLocations {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestGetCardLocations() {
        properties.put("siebeladdr",
                "https://crm.bank.ru/eai_anon_rus/start.swe?SWEExtSource=AnonWebService&SWEExtCmd=Execute");
        properties.put("BspApiAddr", "https://gateway-bsp-chat-prod.apps.ocp.bank.ru");
        CustomConfig.properties = properties;
    }

    @Test
    public void test_getOfficeById() throws IOException {
        Office officeN = getOfficeById("8714");
        System.out.println(officeN.getClearAddress());
        System.out.println(officeN.getSchedule());
    }

    @Test
    public void Bochkov () {
        inputFields.put("siebelId", ApiField.builder().value("X-XXX").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getCardLocations", inputFields);
        printFields(result);
        assertEquals("no cards", result.get("BEgetCardLocationsStatus").getValue());
    }

    @Test
    public void any () {
        inputFields.put("siebelId", ApiField.builder().value("X-XXX").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getCardLocations", inputFields);
        printFields(result);
    }


}