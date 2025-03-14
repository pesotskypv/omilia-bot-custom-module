package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.bank.utilsForTests.printFields;

public class TestSaveEduUserData {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestSaveEduUserData() {
        properties.put("dbserver", "127.0.0.1");
        properties.put("dbname", "OmiliaIntegration");
        properties.put("dbuser", "custommodule");
        properties.put("dbpassword", "custommodule");
        CustomConfig.properties = properties;
    }

    @Test
    public void test1() {
        inputFields.put("BEomniCustomerId", ApiField.builder().value("1").status(FieldStatus.DEFINED).build());
        inputFields.put("FirstName", ApiField.builder().value("a").status(FieldStatus.DEFINED).build());
        inputFields.put("LastName", ApiField.builder().value("b").status(FieldStatus.DEFINED).build());
        inputFields.put("ValidEmail", ApiField.builder().value("test@bank.ru").status(FieldStatus.DEFINED).build());

        Map<String, ApiField> result = botModule.applyCustomAction("saveEduUserData", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEsaveEduUserDataStatus").getValue());
    }
}