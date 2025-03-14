package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.bank.utilsForTests.printFields;

public class TestGetEduData {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestGetEduData() {
        properties.put("dbserver", "127.0.0.1");
        properties.put("dbname", "OmiliaIntegration");
        properties.put("dbuser", "custommodule");
        properties.put("dbpassword", "custommodule");
        CustomConfig.properties = properties;
    }

    @Test
    public void test1() {
        inputFields.put("BEomniCustomerId", ApiField.builder().value("1").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getEduData", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetEduDataStatus").getValue());
        assertNotNull(result.get("FirstName"));
        assertNotNull(result.get("LastName"));
        assertNotNull(result.get("ValidEmail"));
        assertNotNull(result.get("checkpointIisEntryTestResult"));
        assertNotNull(result.get("checkpointIisFinTestResult"));
        assertNotNull(result.get("checkpointIisViktorinaResult"));
        assertNotNull(result.get("checkpointIppEntryTestResult"));
        assertNotNull(result.get("checkpointIppFinTestResult"));
        assertNotNull(result.get("checkpointIppViktorinaResult"));
        assertNotNull(result.get("checkpointMozhnoEntryTestResult"));
        assertNotNull(result.get("checkpointMozhnoFinTestResult"));
        assertNotNull(result.get("checkpointMozhnoViktorinaResult"));
        assertNotNull(result.get("checkpointPifEntryTestResult"));
        assertNotNull(result.get("checkpointPifFinTestResult"));
        assertNotNull(result.get("checkpointPifViktorinaResult"));
        assertNotNull(result.get("checkpointPremEntryTestResult"));
        assertNotNull(result.get("checkpointPremFinTestResult"));
        assertNotNull(result.get("checkpointPremViktorinaResult"));
    }
}