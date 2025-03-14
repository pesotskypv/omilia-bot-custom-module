package ru.bank.bot.nlu;

import java.util.List;

public class CrRegexCondition extends CrCondition {
    private final String regex;

    public CrRegexCondition(String contextRule, int entityIndex, String regex, boolean negate) {
        super(contextRule, entityIndex, negate);
        this.regex = regex;
    }

    @Override
    public boolean matchesCondition(Entity entity, List<Entity> allEntities) {
        boolean output = entity.getCoveredText().matches(regex);

        if /* Если флаг Negate - меняем результат на обратный */ (negate) {
            return !output;
        }
        return output;
    }
}
