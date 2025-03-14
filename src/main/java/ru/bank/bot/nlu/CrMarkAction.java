package ru.bank.bot.nlu;

import java.util.*;

public class CrMarkAction extends CrAction {
    private final MarkActionEntityBuilder markActionEntityBuilder;
    private Integer spansFrom = null;
    private Integer spansTo = null;

    public CrMarkAction(String contextRule, MarkActionEntityBuilder markActionEntityBuilder) {
        super.contextRule = contextRule;
        this.markActionEntityBuilder = markActionEntityBuilder;
    }

    public void setSpansFrom(Integer spansFrom) {
        this.spansFrom = spansFrom;
    }

    public void setSpansTo(Integer spansTo) {
        this.spansTo = spansTo;
    }

    @Override
    public String toString() {
        return "CrMarkAction{" +
                "contextRule='" + contextRule + '\'' +
                ", assignedEntity=" + markActionEntityBuilder.getEntityName() +
                '}';
    }

    @Override
    public void apply(Utterance utterance, Map<Integer, Entity> matchedEntities) {
        // линкуем новую сущность к Part
        Entity assignedEntity = markActionEntityBuilder.build(matchedEntities);
        for (Map.Entry<Integer, Entity> interEntity : matchedEntities.entrySet()) {
            if ((spansFrom == null || interEntity.getKey() >= spansFrom)
                    && (spansTo == null || interEntity.getKey() <= spansTo)) {
                for (Utterance.Part part : interEntity.getValue().getParts()) {
                    assignedEntity.linkToUtterancePart(part);
                }
            }

        }
        utterance.entities.add(assignedEntity); // Добавление новой сущности в общий список
    }

    /**
     * Класс для хранения параметров создаваемого Mark Action'ом Entity, и для создания соответствующего Entity
     */
    public static class MarkActionEntityBuilder {
        private final String entityName;
        /**
         * Набор Feature будущего Entity, у которых явно прописаны значения.
         */
        private final Map<String, String> explicitFeatures = new HashMap<>();
        /**
         * Набор свойств будущего Entity, которые нужно унаследовать от другого Entity
         */
        private final List<LinkedFeature> linkedFeatures = new LinkedList<>();

        public static class LinkedFeature {
            /**
             * Имя будущего Feature
             */
            public final String featureName;
            /**
             * Позиция Entity в паттерне, у которой будем копировать значение Feature
             */
            public final Integer patternPosition;
            /**
             * Имя Feature у Entity-источника, у которых будем копировать значение Feature
             */
            public final String srcFeatureName;

            public LinkedFeature(String featureName, Integer patternPosition, String srcFeatureName) {
                this.featureName = featureName;
                this.patternPosition = patternPosition;
                this.srcFeatureName = srcFeatureName;
            }
        }

        public MarkActionEntityBuilder(String entityName) {
            this.entityName = entityName;
        }

        public String getEntityName() {
            return entityName;
        }

        /**
         * Добавление Feature будущему Entity, значение которого прописывается явно.
         *
         * @param featureName Имя Feature
         * @param value       Значение
         */
        public void addExplicitFeature(String featureName, String value) {
            if (featureName != null && value != null) {
                explicitFeatures.put(featureName, value);
            }
        }

        /**
         * Добавление Feature будущему Entity, значение которого будет унаследовано от другого Entity
         *
         * @param featureName     Имя Feature
         * @param patternPosition Номер позиции Entity в паттерне контекстного правила
         * @param srcFeatureName  Название Feature, значение которого нужно унаследовать
         */
        public void addLinkedFeature(String featureName, Integer patternPosition, String srcFeatureName) {
            if (patternPosition != null && featureName != null && srcFeatureName != null) {
                linkedFeatures.add(new LinkedFeature(featureName, patternPosition, srcFeatureName));
            }
        }

        public Entity build(Map<Integer, Entity> matchedEntities) {
            Entity outputEntity = new Entity(entityName);
            explicitFeatures.forEach(outputEntity::addFeature);

            for (LinkedFeature linkedFeature : linkedFeatures) {
                if (matchedEntities.containsKey(linkedFeature.patternPosition)) {
                    String featureValue = matchedEntities
                            .get(linkedFeature.patternPosition) // находим Entity, у которой будем копировать значение
                            .getFeatureValue(linkedFeature.srcFeatureName); // ... находим значение Feature
                    if (featureValue != null) {
                        outputEntity.addFeature(linkedFeature.featureName, featureValue);
                    }
                }
            }

            return outputEntity;
        }
    }
}
