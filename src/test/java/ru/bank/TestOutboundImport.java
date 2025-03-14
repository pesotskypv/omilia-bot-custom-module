package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.bank.utilsForTests.printFields;

public class TestOutboundImport {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestOutboundImport() {
        properties.put("outboundManagementApi",
                "http://retoutbound.bank.ru:4003/OutadminApi/OutboundManagement.svc/soap");
        properties.put("outboundUser", "omilia");
        properties.put("outboundPass", "Omilia");
        CustomConfig.properties = properties;
    }

    @Test
    public void test1() {
        inputFields.put("uFirstName", ApiField.builder().value("Иван").status(FieldStatus.DEFINED).build());
        inputFields.put("uLastName", ApiField.builder().value("Иванов").status(FieldStatus.DEFINED).build());
        inputFields.put("uPatronymic", ApiField.builder().value("Иванович").status(FieldStatus.DEFINED).build());
        inputFields.put("uPhoneNumber", ApiField.builder().value("9991234567").status(FieldStatus.DEFINED).build());
        inputFields.put("BEoutboundImportAttr1Name", ApiField.builder().value("Param_2").status(FieldStatus.DEFINED)
                .build());
        inputFields.put("BEoutboundImportAttr1Value", ApiField.builder().value("test").status(FieldStatus.DEFINED)
                .build());
        inputFields.put("BEoutboundImportAttr2Name", ApiField.builder().value("Param_5").status(FieldStatus.DEFINED)
                .build());
        inputFields.put("BEoutboundImportAttr2Value", ApiField.builder().value("test5").status(FieldStatus.DEFINED)
                .build());
        inputFields.put("BEoutboundImportCampaignId", ApiField.builder().value("1034").status(FieldStatus.DEFINED)
                .build());
        inputFields.put("BEoutboundImportTimeZoneInfoName", ApiField.builder().value("Russian Standard Time")
                .status(FieldStatus.DEFINED).build());

        Map<String, ApiField> result = botModule.applyCustomAction("outboundImport", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEoutboundImportStatus").getValue());
    }
}