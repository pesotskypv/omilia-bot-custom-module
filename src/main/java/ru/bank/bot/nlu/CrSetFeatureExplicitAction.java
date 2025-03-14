package ru.bank.bot.nlu;

import java.util.Map;

public class CrSetFeatureExplicitAction extends CrAction {
    private final int entityIndex;
    private final String feature;
    private final String value;

    public CrSetFeatureExplicitAction(String contextRule, int entityIndex, String feature, String value) {
        super.contextRule = contextRule;
        this.entityIndex = entityIndex;
        this.feature = feature;
        this.value = value;
    }

    @Override
    public void apply(Utterance utterance, Map<Integer, Entity> matchedEntities) {
        if (matchedEntities.containsKey(entityIndex)) {
            matchedEntities.get(entityIndex).addFeature(feature, value);
        }
    }
}
