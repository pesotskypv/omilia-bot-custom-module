package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.SqlRequest;

import java.util.HashMap;
import java.util.Map;

public class TestGetIcrDbLookup {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    public TestGetIcrDbLookup() {    // Общие параметры
        properties.put("OutboundDbUser", "BotModule");
        properties.put("OutboundDbPass", "BotModule");
        properties.put("OutboundDbHost", "outbound.gts.ru");
        CustomConfig.properties = properties;
    }


    @Test
    public void test1() {
        botModule.dialogData.testFieldsContainer = new HashMap<>();
        botModule.dialogData.testFieldsContainer.put("User.id", "whatsapp");
        botModule.dialogData.testFieldsContainer.put("siebelId", "9648714336");
        SqlRequest.getIcrDbLookup(botModule.dialogData);
    }

}
