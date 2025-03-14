package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.bank.utilsForTests.printFields;

public class TestGetCardsForUnblock {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestGetCardsForUnblock() {
        properties.put("siebeladdr",
                "https://crmcert/siebel/app/eai_anon_rus/rus?SWEExtSource=AnonWebService&SWEExtCmd=Execute");
        CustomConfig.properties = properties;
    }

    @Test
    public void cert_nocard() {
        inputFields.put("clientId", ApiField.builder().value("X-XXX").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getCardsForUnblock", inputFields);
        printFields(result);
        assertEquals("no card", result.get("BEgetCardsForUnblockStatus").getValue());
    }
}