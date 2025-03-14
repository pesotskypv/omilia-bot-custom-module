package ru.bank.bot.nlu;

import java.util.List;

public class CrEntityCondition extends CrCondition {
    private final String operator;
    private final String requiredEntityName;

    public CrEntityCondition(String contextRule, int entityIndex, String operator, String requiredEntityName,
                             boolean negate) {
        super(contextRule, entityIndex, negate);
        this.operator = operator;
        this.requiredEntityName = requiredEntityName;
    }

    @Override
    public boolean matchesCondition(Entity entityToCheck, List<Entity> allEntities) {
        boolean output = false;
        switch (operator) {
            case "PART OF":
                for (Entity iterableEntity : allEntities) {
                    if (iterableEntity.name.equals(requiredEntityName)
                            && iterableEntity.getMinIndex() <= entityToCheck.getMinIndex()
                            && iterableEntity.getMaxIndex() >= entityToCheck.getMaxIndex()
                    ) {
                        output = true;
                    }
                }
                break;
            case "CONTAINS":
                for (Entity iterableEntity : allEntities) {
                    if (iterableEntity.name.equals(requiredEntityName)
                            && entityToCheck.getMinIndex() <= iterableEntity.getMinIndex()
                            && entityToCheck.getMaxIndex() >= iterableEntity.getMaxIndex()
                    ) {
                        output = true;
                    }
                }
                break;
        }

        if /* Если стоит флаг Negate - меняем результат на обратный */ (negate) {
            return !output;
        }
        return output;
    }
}
