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

public class TestGetAnnounces {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestGetAnnounces() {
        properties.put("dbserver", "127.0.0.1");
        properties.put("dbname", "OmiliaIntegration");
        properties.put("dbuser", "custommodule");
        properties.put("dbpassword", "custommodule");
        properties.put("getAnnouncesTable", "dbo.announce");
        CustomConfig.properties = properties;
    }

    @Test
    public void intentMonitoring() {
        inputFields.put("dtName", ApiField.builder().value("Monitoring").status(FieldStatus.DEFINED).build());
        inputFields.put("id", ApiField.builder().value("web").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getAnnounces", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetAnnouncesStatus").getValue());
        assertEquals("1", result.get("announcementsNumber").getValue());
        assertEquals("3BR7T152KD5S", result.get("announce1").getValue());
    }

}