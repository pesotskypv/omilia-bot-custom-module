package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;

import java.util.HashMap;
import java.util.Map;

import static ru.bank.bot.HttpRequests.sendAnswerToHr;
import static ru.bank.utilsForTests.printFields;

public class TestSendAnswerToHr {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestSendAnswerToHr() {
        properties.put("HrAbpm", "https://rsb-dialer-content-service-abpm-prod.apps.ocp.bank.ru");
        CustomConfig.properties = properties;
    }

    @Test
    public void test1() {
        botModule.dialogData.testFieldsContainer = new HashMap<>();
        botModule.dialogData.testFieldsContainer.put("ANI", "79991234567");
        Map<String, ApiField> result = sendAnswerToHr("Y", botModule.dialogData);
        printFields(result);
        Assertions.assertNotNull(result.get("BEsendAnswerToHrStatus"));
    }
}
