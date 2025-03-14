package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static ru.bank.bot.Auto.sendLead;
import static ru.bank.utilsForTests.printFields;
import static org.junit.jupiter.api.Assertions.*;


public class TestSendLeadAuto {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    @Test
    public void direct() {
        Map<String, ApiField> result = sendLead("Иван","Иванов","-","01.01.2000",
                "+7999 1234567","aaa@bbb.cc","abc");
        printFields(result);
        String status = result.get("BEsendLeadAutoStatus").getValue();
        assertTrue(status.contains("ok") || status.contains("sentBefore"));
    }
}