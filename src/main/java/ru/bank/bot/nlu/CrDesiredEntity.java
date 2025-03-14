package ru.bank.bot.nlu;

import java.util.LinkedList;
import java.util.List;

/**
 * Желаемая (Desired) Entity на конкретной позиции позиции Context Rule.
 * Содержит требуемое название, Conditions (Feature, Entity, Regex) и флаг обязательности в паттерне.
 */
public class CrDesiredEntity {
    /**
     * Имя Entity
     */
    private final String name;
    /**
     * Обязательно ли наличие этого Entity?
     */
    private final boolean mandatory;
    private final List<CrCondition> crConditions = new LinkedList<>();

    public CrDesiredEntity(String name, boolean mandatory) {
        this.name = name;
        this.mandatory = mandatory;
    }

    /**
     * Добавление Condition.
     *
     * @param crCondition Condition
     */
    public void addCondition(CrCondition crCondition) {
        crConditions.add(crCondition);
    }


    /**
     * Проверка Entity на соответствие Desired Entity (название + все Conditions)
     *
     * @param allEntities Для проверки условий, связанных с включением одной сущности в другую.
     */
    public boolean matches(Entity entityToCheck, List<Entity> allEntities) {
        return name.equals(entityToCheck.name)
                && crConditions.stream().allMatch(
                crCondition -> crCondition.matchesCondition(entityToCheck, allEntities));
    }

    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public String toString() {
        return "CrDesiredEntity{" +
                "name='" + name + '\'' +
                ", mandatory=" + mandatory +
                ", crConditions=" + crConditions +
                '}';
    }
}
