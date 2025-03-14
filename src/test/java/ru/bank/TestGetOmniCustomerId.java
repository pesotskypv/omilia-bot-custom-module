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

public class TestGetOmniCustomerId {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestGetOmniCustomerId() {
        properties.put("omniDbHost", "10.46.65.176");
        properties.put("omniDbName", "cti_omni_prod");
        properties.put("omniDbUser", "dbreport");
        properties.put("omniDbPass", "7jG%b6Dc4#");
        CustomConfig.properties = properties;
    }

    @Test
    public void test1() {
        inputFields.put("activityIdNow", ApiField.builder().value("75956f85-97c0-425a-8bb6-2cae77133df2")
                .status(FieldStatus.DEFINED).build());

        Map<String, ApiField> result = botModule.applyCustomAction("getOmniCustomerId", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetOmniCustomerIdStatus").getValue());
        assertEquals("5445768", result.get("BEomniCustomerId").getValue());
    }
}