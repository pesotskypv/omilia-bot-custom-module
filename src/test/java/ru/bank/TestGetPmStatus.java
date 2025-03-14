package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.bank.utilsForTests.printFields;

public class TestGetPmStatus {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestGetPmStatus() {
        properties.put("omniDbHost", "127.0.0.1");
        properties.put("omniDbName", "omni_prod");
        properties.put("omniDbUser", "dbreport");
        properties.put("omniDbPass", "dbreport");
        CustomConfig.properties = properties;
    }

    @Test
    public void testRequestType437() {
        inputFields.put("RequestType", ApiField.builder().value("437").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getPmStatus", inputFields);
        printFields(result);
        assertEquals(result.get("BEgetPmStatus").getValue(), "ok");

        assertTrue(result.containsKey("BEpmActive"));
        assertTrue(result.get("BEpmActive").getValue().equals("true")
                || result.get("BEpmActive").getValue().equals("false") );

        assertTrue(result.containsKey("BEpmLogged"));
        assertTrue(result.get("BEpmLogged").getValue().equals("true")
                || result.get("BEpmLogged").getValue().equals("false") );

        assertTrue(result.containsKey("BEpmReady"));
        assertTrue(result.get("BEpmReady").getValue().equals("true")
                || result.get("BEpmReady").getValue().equals("false") );
    }

    @Test
    public void testRequestType9999() {
        inputFields.put("RequestType", ApiField.builder().value("9999").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getPmStatus", inputFields);
        printFields(result);
        assertEquals(result.get("BEgetPmStatus").getValue(), "error");
    }

    @Test
    public void testRequestTypeAAAA() {
        inputFields.put("RequestType", ApiField.builder().value("AAAA").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getPmStatus", inputFields);
        printFields(result);
        assertEquals(result.get("BEgetPmStatus").getValue(), "error");
    }

}