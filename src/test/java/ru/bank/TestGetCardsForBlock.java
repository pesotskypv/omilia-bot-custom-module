package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static ru.bank.utilsForTests.printFields;

public class TestGetCardsForBlock {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestGetCardsForBlock() {
        properties.put("siebeladdr",
                "https://crmcert/siebel/app/eai_anon_rus/rus?SWEExtSource=AnonWebService&SWEExtCmd=Execute");
        CustomConfig.properties = properties;
    }

    @Test
    public void cert_nocard() {
        inputFields.put("clientId", ApiField.builder().value("X-XXX").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getCardsForBlock", inputFields);
        printFields(result);
        assertEquals("no card", result.get("BEgetCardsForBlockStatus").getValue());
    }

    @Test
    public void cert_has_active_cards() {
        inputFields.put("clientId", ApiField.builder().value("X-XXX").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getCardsForBlock", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetCardsForBlockStatus").getValue());
        assertEquals(4, result.get("BEcard1").getValue().length());
        assertNotNull(result.get("BEcardName1"));
    }
}
