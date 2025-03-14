package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static ru.bank.utilsForTests.printFields;

public class TestGetNearestOffices {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestGetNearestOffices() {
        properties.put("yandexapikey", "45dfde94-12dc-4a8f-ba29-555eff22b6d5");
        properties.put("bspApiAddr", "https://gateway-bsp-chat-prod.apps.ocp.bank.ru");
        CustomConfig.properties = properties;
    }

    @Test
    public void belorusskaya_ulservicing() {
        inputFields.put("BEofficeCity", ApiField.builder().value("москва").status(FieldStatus.DEFINED).build());
        inputFields.put("BEofficeAddress", ApiField.builder().value("метро белорусская").status(FieldStatus.DEFINED)
                .build());
        inputFields.put("BEofficeService", ApiField.builder().value("ulservicing").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getNearestOffices", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetNearestOfficesStatus").getValue());
        assertNotNull(result.get("BEofficeAddress1").getValue());
        assertNotNull(result.get("BEofficeDistance1").getValue());
    }

    @Test
    public void korolyova_ulservicing() {
        inputFields.put("BEofficeCity", ApiField.builder().value("москва").status(FieldStatus.DEFINED).build());
        inputFields.put("BEofficeAddress", ApiField.builder().value("улица Академика Королева, 12")
                .status(FieldStatus.DEFINED).build());
        inputFields.put("BEofficeService", ApiField.builder().value("ulservicing").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getNearestOffices", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetNearestOfficesStatus").getValue());
        assertNotNull(result.get("BEofficeAddress1").getValue());
        assertFalse(result.get("BEofficeAddress1").getValue().contains("Королева"));
        assertNotNull(result.get("BEofficeDistance1").getValue());
    }

    @Test
    public void prosp_mira_cash() {
        inputFields.put("BEofficeCity", ApiField.builder().value("москва").status(FieldStatus.DEFINED).build());
        inputFields.put("BEofficeAddress", ApiField.builder().value("проспект Мира, 36").status(FieldStatus.DEFINED)
                .build());
        inputFields.put("BEofficeService", ApiField.builder().value("operations").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getNearestOffices", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetNearestOfficesStatus").getValue());
        assertNotNull(result.get("BEofficeAddress1").getValue());
        assertFalse(result.get("BEofficeAddress1").getValue().contains("проспект Мира, 36"));
        assertNotNull(result.get("BEofficeDistance1").getValue());
    }

    @Test
    public void samara() {
        inputFields.put("BEofficeCity", ApiField.builder().value("самара").status(FieldStatus.DEFINED).build());
        inputFields.put("BEofficeAddress", ApiField.builder().value("московское шоссе").status(FieldStatus.DEFINED)
                .build());
        inputFields.put("BEofficeService", ApiField.builder().value("cashOperations").status(FieldStatus.DEFINED)
                .build());
        Map<String, ApiField> result = botModule.applyCustomAction("getNearestOffices", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEgetNearestOfficesStatus").getValue());
        assertNotNull(result.get("BEofficeAddress1").getValue());
        assertNotNull(result.get("BEofficeDistance1").getValue());
    }

}
