package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.service.BotService;

import java.util.HashMap;
import java.util.Map;

public class TestGetIcrBalanceInfo {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestGetIcrBalanceInfo() {
        properties.put("BspAddr", "https://gateway-drbs-prod.apps.ocp.bank.ru");
        properties.put("BspApiAddr", "https://gateway-bsp-chat-prod.apps.ocp.bank.ru");
        CustomConfig.properties = properties;
    }

    @Test
    public void curier() {
        botModule.dialogData.testFieldsContainer = new HashMap<>();
        botModule.dialogData.testFieldsContainer.put("siebelId", "X-XXX");
        BotService.getIcr(botModule.dialogData);
    }
}
