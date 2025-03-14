package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static ru.bank.utilsForTests.printFields;


public class TestSelectRandomQuestions {
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    @Test
    public void get10() {
        inputFields.put("BEtotalQuestions", ApiField.builder().value("31").status(FieldStatus.DEFINED).build());
        int BEhowMuchQuestionsNeed = 10;
        inputFields.put("BEhowMuchQuestionsNeed", ApiField.builder().value(String.valueOf(BEhowMuchQuestionsNeed))
                .status(FieldStatus.DEFINED).build());
        Map<String, ApiField> result = botModule.applyCustomAction("selectRandomQuestions", inputFields);
        printFields(result);
        // проверка что сервис возвращает 10 переменных + сатус
        assertEquals(BEhowMuchQuestionsNeed + 1, result.size());
        assertEquals("ok", result.get("BEselectRandomQuestionsStatus").getValue());
    }
}
