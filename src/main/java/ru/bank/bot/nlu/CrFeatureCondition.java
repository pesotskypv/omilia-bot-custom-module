package ru.bank.bot.nlu;

import java.util.LinkedList;
import java.util.List;

public class CrFeatureCondition extends CrCondition {
    /**
     * Споисок допустимых значений для Feature
     */
    private final List<String> allowedValues = new LinkedList<>();
    private final String feature;

    public CrFeatureCondition(String contextRule, int entityIndex, String feature, boolean negate) {
        super(contextRule, entityIndex, negate);
        this.feature = feature;
    }

    @Override
    public boolean matchesCondition(Entity entity, List<Entity> allEntities) {
        boolean output;
        String checkedFeatureValue = entity.getFeatureValue(feature);
        if /* Entity не содержит свойство */ (entity.getFeatureValue(feature) == null) {
            output = false;
        } else output = allowedValues.contains(checkedFeatureValue);

        if /* Если стоит флаг Negate - меняем результат на обратный */ (negate) {
            return !output;
        }
        return output;
    }

    public void addValue(String value) {
        if (value != null) {
            allowedValues.add(value);
        }
    }

    @Override
    public String toString() {
        String output = "FeatureCondition{" +
                "feature='" + feature + '\'' +
                ", allowedValues=" + allowedValues;
        if (negate) {
            output += ", negate=true";
        }
        output += '}';
        return output;
    }
}
