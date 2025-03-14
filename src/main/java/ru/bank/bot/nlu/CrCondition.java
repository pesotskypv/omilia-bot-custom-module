package ru.bank.bot.nlu;

import java.util.List;

public abstract class CrCondition {
    public String contextRule;
    public int entityIndex;
    boolean negate;

    public CrCondition(String contextRule, int entityIndex, boolean negate) {
        this.entityIndex = entityIndex;
        this.contextRule = contextRule;
        this.negate = negate;
    }

    public abstract boolean matchesCondition(Entity entityToCheck, List<Entity> allEntities);
}
