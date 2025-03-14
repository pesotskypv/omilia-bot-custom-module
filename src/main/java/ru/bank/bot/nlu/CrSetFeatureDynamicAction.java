package ru.bank.bot.nlu;

import java.util.Map;

public class CrSetFeatureDynamicAction extends CrAction {
    private final int entityIndex;
    private final String feature;
    private final int srcEntityIndex;
    private final String srcFeature;


    public CrSetFeatureDynamicAction(String contextRule, int entityIndex, String feature, int srcEntityIndex
            , String srcFeature) {
        super.contextRule = contextRule;
        this.entityIndex = entityIndex;
        this.feature = feature;
        this.srcEntityIndex = srcEntityIndex;
        this.srcFeature = srcFeature;
    }

    @Override
    public void apply(Utterance utterance, Map<Integer, Entity> entities) {
        if (entities.containsKey(entityIndex) && entities.containsKey(srcEntityIndex)) {
            Entity dstEntity = entities.get(entityIndex);
            Entity srcEntity = entities.get(srcEntityIndex);

            String featureValue = srcEntity.getFeatureValue(srcFeature);
            if (featureValue != null) {
                dstEntity.addFeature(feature, featureValue);
            }
        }
    }
}
