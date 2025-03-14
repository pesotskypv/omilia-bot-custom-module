package ru.bank.bot.utils;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для визуального облегчения синтаксиса при формировании Output Map.
 */
public class OutputMap {
    Map<String, ApiField> output = new HashMap<>();

    /**
     * Возвращает сформированный Output Map. Для return в бота.
     * @return
     */
    public Map<String, ApiField> get() {
        return output;
    }

    /**
     * Добавить филд и значение в Output
     * @param field Имя филда
     * @param value Значение
     */
    public void add(String field, String value) {
        if (field != null && value != null) {
            output.put(field, ApiField.builder().name(field).value(value).status(FieldStatus.DEFINED).build());
        }
    }

    public int size() {
        return output.size();
    }

    /**
     * Частая операция - указываем статусное поле и возвращаем выходной массив.
     */
    public Map<String, ApiField> setStatusAndReturn(String field, String value) {
        add(field, value);
        return output;
    }

    /**
     * Содержит ли OutputMap ключ, похожий на pattern (case insensitive)
     */
    public boolean isContainsKeyLike(String pattern) {
        return output.keySet().stream().anyMatch(x -> x.toLowerCase().contains(pattern.toLowerCase()));
    }
}