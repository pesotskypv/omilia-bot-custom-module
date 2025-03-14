package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;
import org.junit.jupiter.api.Test;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.bot.nlu.CrDesiredEntity;
import ru.bank.bot.nlu.CrFeatureCondition;
import ru.bank.bot.nlu.Entity;
import ru.bank.bot.nlu.Utterance;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestNlu {
    Map<String, Object> properties = new HashMap<>();
    Map<String, ApiField> inputFields = new HashMap<>();
    BotModule botModule = new BotModule();

    TestNlu() {
        properties.put("dbserver", "127.0.0.1");
        properties.put("dbname", "OmiliaIntegration");
        properties.put("dbuser", "custommodule");
        properties.put("dbpassword", "custommodule");
        properties.put("getAnnouncesTable", "dbo.announce");
        CustomConfig.properties = properties;
    }

    /**
     * 1. Тестирование Desired Entity, которая создаётся на основе паттерна контекстных правил по имени Entity;
     * 2. Тестирование Feature Condition
     */
    @Test
    public void testDesiredEntity() {
        // Проверяемая сущность: Product{Value=card, Type=bank}
        Entity productCard = new Entity("Product");
        productCard.addFeature("Value", "card");
        productCard.addFeature("Type", "bank");

        // Желаемая сущность: Product - TRUE
        CrDesiredEntity desiredProduct = new CrDesiredEntity("Product", true);
        assertTrue(desiredProduct.matches(productCard, null));

        // Желаемая сущность: Action  - FALSE
        CrDesiredEntity desiredAction = new CrDesiredEntity("Action", true);
        assertFalse(desiredAction.matches(productCard, null));

        // Желаемая сущность: Product{Value=card} - TRUE
        CrDesiredEntity desiredProductNvalue = new CrDesiredEntity("Product", true);
        CrFeatureCondition crFeatureCondition = new CrFeatureCondition("Test", 0, "Value", false);
        crFeatureCondition.addValue("card");
        desiredProduct.addCondition(crFeatureCondition);
        assertTrue(desiredProductNvalue.matches(productCard, null));

        // Желаемая сущность: Product{Value=card,account; Type=bank} - TRUE
        CrDesiredEntity desiredProductNvalueNtype = new CrDesiredEntity("Product", true);
        CrFeatureCondition conditionValueCard = new CrFeatureCondition("Test", 0, "Value", false);
        conditionValueCard.addValue("card");
        conditionValueCard.addValue("account");
        desiredProduct.addCondition(conditionValueCard);
        CrFeatureCondition conditionTypeBank = new CrFeatureCondition("Test", 0, "Type", false);
        conditionTypeBank.addValue("bank");
        desiredProduct.addCondition(conditionTypeBank);
        assertTrue(desiredProductNvalueNtype.matches(productCard, null));

        // Желаемая сущность: Product{Value=account} - FALSE
        CrDesiredEntity desiredProductNvalueAccount = new CrDesiredEntity("Product", true);
        CrFeatureCondition crFeatureConditionAccount = new CrFeatureCondition("Test", 0, "Value", false);
        crFeatureConditionAccount.addValue("account");
        desiredProductNvalueAccount.addCondition(crFeatureConditionAccount);
        assertFalse(desiredProductNvalueAccount.matches(productCard, null));
    }

    @Test
    public void any3() throws SQLException {
        Map<String, ApiField> output = new HashMap<>();
        Utterance utterance = new Utterance("заблокировать карту" // заблокировать карту ; заявление-запрос
                , "Intent"
                , ""
                , ""
                , new DialogData());
        utterance.runNlu(output);
        utterance.printAllIntentsWithScore();
    }

    @Test
    public void any1() throws SQLException {
        Map<String, ApiField> output = new HashMap<>();
        Utterance utterance = new Utterance("Цифровой код", "Intent", "", "",
                new DialogData());
        utterance.runNlu(output);
        utterance.printAllIntentsWithScore();
    }
}
