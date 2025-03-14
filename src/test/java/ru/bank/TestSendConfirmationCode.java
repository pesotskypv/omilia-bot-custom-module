package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static ru.bank.utilsForTests.printFields;

public class TestSendConfirmationCode {
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    @Test
    public void Pesotsky() {
        inputFields.put("ValidEmail", ApiField.builder().value("pesotskypv@ya.ru").status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("sendConfirmationCode", inputFields);
        printFields(result);
        assertNotNull(result.get("BEsendConfirmationCodeStatus"));
    }
}
