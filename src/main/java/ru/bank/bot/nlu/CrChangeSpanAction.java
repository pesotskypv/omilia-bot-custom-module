package ru.bank.bot.nlu;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

public class CrChangeSpanAction extends CrAction {
    private final int entityIndex;
    private final int beginIndex;
    private final int endIndex;


    public CrChangeSpanAction(String contextRule, int entityIndex, int beginIndex, int endIndex) {
        super.contextRule = contextRule;
        this.entityIndex = entityIndex;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void apply(Utterance utterance, Map<Integer, Entity> entities) {
        Optional<Entity> currentEntity = Optional.ofNullable(entities.get(entityIndex));

        LinkedHashSet<Utterance.Part> newSpan = new LinkedHashSet<>();
        for (int iteratedIndex = beginIndex; iteratedIndex <= endIndex; iteratedIndex++) {
            if (entities.containsKey(iteratedIndex)) {
                newSpan.addAll(entities.get(iteratedIndex).getParts());
            }
        }

        currentEntity.ifPresent(entity -> entity.setNewSpan(newSpan));
    }
}
