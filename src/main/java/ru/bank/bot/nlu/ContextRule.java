package ru.bank.bot.nlu;

import com.google.gson.Gson;
import ru.bank.bot.DialogData;

import java.util.*;

import static ru.bank.bot.utils.Utils.toDiamantLog;

public class ContextRule {
    public String name;
    private final Map<Integer, CrDesiredEntity> desiredEntities = new HashMap<>();
    private final List<CrAction> crActions = new LinkedList<>();
    private final String context;

    public ContextRule(String name, String context) {
        this.name = name;
        this.context = context;
    }

    /**
     * Добавление Condition.
     *
     * @param crCondition Condition
     */
    public void addCondition(CrCondition crCondition) {
        if (crCondition.contextRule.equals(this.name)) { // Если Condition для этого Context Rule
            if (desiredEntities.containsKey(crCondition.entityIndex)) {
                desiredEntities.get(crCondition.entityIndex).addCondition(crCondition); // new
            }
        }
    }

    /**
     * Добавление Action (с проверкой соответствия имени контекстного правила).
     *
     * @param crAction Action
     */
    public void addAction(CrAction crAction) {
        if (crAction.contextRule.equals(this.name)) {
            crActions.add(crAction);
        }
    }

    /**
     * Проверить, соответствует ли фраза контекстному правилу, и если да, то применить его.
     *
     * @param utterance    Фраза клиента
     * @param appliedRules Список применённых контекстных правил
     * @param dialogData   Для логирования в лог диалога
     */
    public void checkAndApply(Utterance utterance, List<String> appliedRules, DialogData dialogData) {
        // Проверка контекста
        if (this.context != null && !utterance.contexts.contains(this.context)) {
            return;
        }

        for (int ix = 0; ix < utterance.partsCount(); ix++) { // Перебираем индексы фразы для первого элемента
            for (Entity fstEntity : utterance.getEntitiesBeginWithIndex(ix)) { // Перебираем сущности для первого элемента
                // Список совпавших сущностей с привязкой к позиции в паттерне
                Map<Integer, Entity> matchedEntitiesMap = new HashMap<>();
                // Последняя совпавшая сущность - для поиска соседа если не найдены необязательные сущности.
                Optional<Entity> lastMatchedEntity = Optional.empty();
                // Entity, которая проверяется на текущем круге на соответствие позиции паттерна (ixP)
                Optional<Entity> currentEntity = Optional.of(fstEntity);

                for (int ixP = 0; ixP < desiredEntities.size(); ixP++) {
                    CrDesiredEntity desiredEntity = desiredEntities.get(ixP);

                    if (currentEntity.isPresent() && desiredEntity.matches(currentEntity.get(), utterance.entities)) {
                        lastMatchedEntity = currentEntity;
                        matchedEntitiesMap.put(ixP, currentEntity.get());

                        // Для отладки
                        String contextRuleName = "Service_digital_profile_1";
                        if (name.equals(contextRuleName)) {
                            toDiamantLog(dialogData, "Для отладки contextRuleName: " + contextRuleName);
                        }

                    } else if (desiredEntity.isMandatory()) { // Текущая Desired Entity обязательная и не совпала
                        matchedEntitiesMap.clear(); // Очищаем список совпадений ...
                        break; // ... и прекращаем этот круг
                    }

                    // Берём последнее совпадение и пытаемся найти совпадающего соседа.
                    // lastMatchedEntity может быть пустым если первая сущность не совпала, но была необязательной.
                    if (lastMatchedEntity.isPresent()) {
                        currentEntity = getMatchingRightNeighbor(utterance, lastMatchedEntity.get(), ixP + 1);
                    }
                }

                // Применение действий CR
                if (!matchedEntitiesMap.isEmpty()) { // Если список пустой - значит правило не совпало
                    crActions.forEach(
                            crAction -> crAction.apply(utterance, matchedEntitiesMap));
                    appliedRules.add(this.name);
                }
            }
        }
    }

    /**
     * Получение соседней справа Entity, которая соответствует паттерну. NEW
     *
     * @param utterance  Для получения
     * @param currEntity Текущая Entity
     * @param patternPos Позиция Desired Entity в паттерне, с условиями которой должна совпадать currEntity
     */
    private Optional<Entity> getMatchingRightNeighbor(Utterance utterance, Entity currEntity, int patternPos) {
        if (patternPos >= desiredEntities.size()) {
            return Optional.empty();
        }
        CrDesiredEntity desiredEntity = desiredEntities.get(patternPos);

        return utterance.getRightNeighbors(currEntity)
                .filter(neighbor -> desiredEntity.matches(neighbor, utterance.getAllEntities()))
                .findFirst();
    }

    /**
     * Добавление желаемой Entity в паттерн.
     */
    public void addDesiredEntity(int postition, String entityName, Boolean mandatory) {
        if (entityName != null) {
            desiredEntities.put(postition, new CrDesiredEntity(entityName, mandatory));
        }
    }


    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
