package ru.bank;


import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static ru.bank.utilsForTests.printFields;

public class TestGetCcReport {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    public TestGetCcReport() {    // Общие параметры
        properties.put("ccReportDbHost", "193.48.0.237");
        properties.put("ccReportDbName", "ContactCenter");
        properties.put("ccReportDbUser", "omilia");
        properties.put("ccReportDbPass", "apiNVoLoGYrO");
        CustomConfig.properties = properties;
    }


    @Test
    public void АНТ() {
        inputFields.put("BEgetCcReportName", ApiField.builder().value("CUSTOM.АНТ").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getCcReport", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetCcReportStatus").getValue());
        assertTrue(result.size() > 1);
    }
}