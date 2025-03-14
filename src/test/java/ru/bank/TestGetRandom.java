package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.bank.utilsForTests.printFields;

public class TestGetRandom {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    @Test
    public void TestGetRandom() {
        int BErandomMax = 3;
        inputFields.put("BErandomMax", ApiField.builder().value(String.valueOf(BErandomMax))
                .status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("getRandom", inputFields);
        printFields(result);
        int BErandom = Integer.parseInt(result.get("BErandom").getValue());
        assertTrue(BErandom > 0 && BErandom <= BErandomMax);
    }
}
