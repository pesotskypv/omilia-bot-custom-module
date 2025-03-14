package ru.bank.bot.nlu;

import java.util.Map;

public class CrUnmarkAction extends CrAction {
    private final int entityIndex;

    public CrUnmarkAction(String contextRule, int entityIndex) {
        super.contextRule = contextRule;
        this.entityIndex = entityIndex;
    }

    @Override
    public void apply(Utterance utterance, Map<Integer, Entity> entities) {
        if (entities.containsKey(entityIndex)) {
            Entity entityToRemove = entities.get(entityIndex);
            utterance.entities.remove(entityToRemove);
        }
    }
}
