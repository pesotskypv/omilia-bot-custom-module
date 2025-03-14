package ru.bank.bot.nlu;

import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestNlu {
    public static void main(String[] args) throws SQLException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("dbserver", "193.48.0.227");
        properties.put("dbname", "CERT_OmiliaIntegration");
        properties.put("dbuser", "custommodule");
        properties.put("dbpassword", "6^f#5wwQ");
        CustomConfig.properties = properties;

        String fieldToElicit = "Intent";
        String target = null;
        String action = null;

        for (int i = 0; i < args.length; i++) {
            if (i == 0) {
                fieldToElicit = args[0]; // Первый параметр - это fieldToElicit
            } else if (i == 1) {
                target = args[1]; // Второй параметр - имя таргета
            } else if (i == 2) {
                action = args[2]; // Третий параметр - имя действия
            } else { // Четвёртый и последующие - фразы
                Utterance utterance = new Utterance(args[i]
                        , fieldToElicit
                        , target
                        , action
                        , new DialogData());
                utterance.runNlu(new HashMap<>());
                utterance.printAllIntentsWithScore();
                System.out.println(); // Пустая строка между результатами
            }
        }
    }
}
