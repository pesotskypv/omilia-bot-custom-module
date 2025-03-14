package ru.bank.bot;

import com.omilia.diamant.loggers.GenericLogger;
import ru.bank.bot.nlu.ContextRule;
import ru.bank.bot.nlu.CrAction;
import ru.bank.bot.nlu.CrCondition;

import java.util.*;

import static ru.bank.util.Utils.genericLog;
import static ru.bank.util.Utils.genericLogErr;

/**
 * Класс отвечает за хранение данных, общих для каждого сценария.
 * Название CustomConfig было взято, когда класс хранил только параметры DiaManT_Custom_Config.xml
 */
public class CustomConfig {
    public static Map<String, Object> properties = null;
    public static GenericLogger glogger = null;

    /**
     * Список контекстных правил (содержит правильный порядок)
     */
    private static List<ContextRule> contextRules = new LinkedList<>();

    /**
     * Словарь контекстных правил для быстрого поиска по имени.
     */
    private static final Map<String, ContextRule> contextRulesNameMap = new HashMap<>();

    /**
     * Получить список контекстных правил.
     */
    public static List<ContextRule> getContextRules() {
        if (contextRules.isEmpty()) {
            reloadContextRules();
        }
        return contextRules;
    }

    /**
     * Получить контекстное правило по имени.
     * Если правил в памяти нет - будут загружены из БД.
     */
    public static Optional<ContextRule> getContextRuleByName(String contextRuleName) {
        if (contextRulesNameMap.isEmpty()) {
            for (ContextRule iterContextRule : getContextRules()) {
                contextRulesNameMap.put(iterContextRule.name, iterContextRule);
            }
        }

        return Optional.ofNullable(contextRulesNameMap.get(contextRuleName));
    }

    /**
     * Получение контекстных правил из БД
     */
    public static void reloadContextRules() {
        try {
            // Получение контекстных правил
            contextRules = SqlRequest.getAllContextRules();
            if (contextRules.isEmpty()) {
                genericLog("Нет записей в таблице nlu.ContextRules");
                return;
            }

            // Получение Conditions
            List<CrCondition> crConditions = new LinkedList<>();
            crConditions.addAll(SqlRequest.getAllCrRegexConditions());
            crConditions.addAll(SqlRequest.getAllCrFeatureConditions());
            crConditions.addAll(SqlRequest.getAllCrEntityConditions());
            crConditions.forEach(
                    crCondition -> getContextRuleByName(crCondition.contextRule).ifPresent(
                            rule -> rule.addCondition(crCondition)));

            // Actions
            List<CrAction> crActions = new LinkedList<>();
            crActions.addAll(SqlRequest.getAllMarkActions());
            crActions.addAll(SqlRequest.getAllSetFeatureExplicitActions());
            crActions.addAll(SqlRequest.getAllSetFeatureDynamicActions());
            crActions.addAll(SqlRequest.getAllUnmarkActions());
            crActions.addAll(SqlRequest.getAllChangeSpanActions());
            crActions.forEach(crAction -> getContextRuleByName(crAction.contextRule).ifPresent(
                    rule -> rule.addAction(crAction)));

        } catch (Exception e) {
            genericLogErr("Не удалось загрузить контекстные правила из БД: " + e);
        }
    }

    /**
     * Получение значения Property из DiaManT_Custom_Config
     */
    public Optional<String> getProperty(String name) {
        Optional<String> output = Optional.empty();

        if (properties.containsKey(name)) {
            try {
                String propertyValue = (String) properties.get(name);
                output = Optional.ofNullable(propertyValue);
            } catch (Exception ignored) {
            }
        }
        return output;
    }
}
