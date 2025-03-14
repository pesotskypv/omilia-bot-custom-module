package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.bank.utilsForTests.printFields;

public class TestCreateCommunication {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestCreateCommunication() {
        // CERT:
        properties.put("siebeladdr",
                "https://crmcert/siebel/app/eai_anon_rus/rus?SWEExtSource=AnonWebService&SWEExtCmd=Execute");
        // TEST:
        properties.put("siebeladdr",
                "https://rsbt-asthumfct.tbank.ru:8443/siebel/app/eai_anon_rus/rus?SWEExtSource=AnonWebService&amp;" +
                        "SWEExtCmd=Execute");
        CustomConfig.properties = properties;
    }

    @Test
    public void test1() {
        inputFields.put("productCode", ApiField.builder().value("Кредитная карта").status(FieldStatus.DEFINED).build());
        inputFields.put("source", ApiField.builder().value("Сайт Банка").status(FieldStatus.DEFINED).build());
        inputFields.put("uFirstName", ApiField.builder().value("FirstName").status(FieldStatus.DEFINED).build());
        inputFields.put("uPhoneNumber", ApiField.builder().value("+79991234567").status(FieldStatus.DEFINED).build());
        inputFields.put("siebelId", ApiField.builder().value("1-XXX").status(FieldStatus.DEFINED).build());

        Map<String, ApiField> result = botModule.applyCustomAction("createCommunication", inputFields);
        printFields(result);
        assertEquals("ok", result.get("BEcreateCommunicationStatus").getValue());
    }
}
