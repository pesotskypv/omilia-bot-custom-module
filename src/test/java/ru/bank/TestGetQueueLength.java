package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static ru.bank.utilsForTests.printFields;

public class TestGetQueueLength {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestGetQueueLength() {
        properties.put("omniAddr", "https://omni.bank.ru");
        CustomConfig.properties = properties;
    }

    @Test
    public void test1() {
        inputFields.put("id", ApiField.builder().value("chatevoios").status(FieldStatus.DEFINED).build());
        inputFields.put("ActivityId", ApiField.builder().value("56891a66-1c7d-49a5-9b03-ad85217e5ce6")
                .status(FieldStatus.DEFINED).build());
        inputFields.put("Intent", ApiField.builder().value("int_Test").status(FieldStatus.DEFINED).build());
        inputFields.put("BEqueueThresholdMedium", ApiField.builder().value("5").status(FieldStatus.DEFINED).build());
        inputFields.put("BEqueueThresholdHigh", ApiField.builder().value("100").status(FieldStatus.DEFINED).build());

        Map<String, ApiField> result = botModule.applyCustomAction("getQueueLength", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetQueueLengthStatus").getValue());
        assertNotNull(result.get("BEqueueLength"));
        assertNotNull(result.get("BEqueueThreshold"));
    }
}