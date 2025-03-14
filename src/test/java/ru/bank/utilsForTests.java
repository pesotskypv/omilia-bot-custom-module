package ru.bank;

import com.omilia.diamant.dialog.components.fields.ApiField;

import java.util.Map;

public class utilsForTests {
    public static void printFields(Map<String, ApiField> output) {
        output.entrySet().stream()
                .sorted((x, y) -> x.getKey().compareTo(y.getKey()))
                .forEach(x -> System.out.println(x.getKey() + " = " + x.getValue().getValue()));
    }
}
