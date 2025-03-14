package ru.bank.bot.nlu;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Условие интента
 */
public class Constraint {
    public final String name;
    public final String entityName;
    public final Operator operator;
    public final List<Feature> requiredFeatures = new LinkedList<>();
    public boolean matched = false;
    public int score = 0;

    public Constraint(String name, String entityName, String operator) {
        this.name = name;
        this.entityName = entityName;
        this.operator = Operator.valueOf(operator);
    }

    public void addFeature(String name, String value) {
        if (name != null && value != null) {
            requiredFeatures.add(new Feature(name, value));
        }
    }


    /**
     * Проверить Entity на соответствие Constraint'у и пометить его совпавшим (или нет).
     */
    public void checkEntitiesNremember(List<Entity> entities) {

        switch (operator) {
            case EXISTS:
                if /* Если условие без свойств */ (requiredFeatures.isEmpty()
                        && entities.stream().anyMatch(ent -> ent.name.equals(this.entityName))) {
                    this.score = 1;
                    matched = true;
                }

                for (Entity iterEntity : entities) {
                    if (iterEntity.name.equals(this.entityName)
                            && requiredFeatures.stream().allMatch(ftr -> ftr.matchesEntity(iterEntity))) {
                        score = 1 + requiredFeatures.size();
                        matched = true;
                    }
                }
                break;

            case NOT_EXISTS:
                if (entities.stream().noneMatch(ent -> ent.name.equals(this.entityName))) {
                    this.score = 1;
                    matched = true;
                }
                break;

            case EXISTS_OPTIONALLY:
                matched = true; // Это условие всегда true, но score надо посчитать по совпадениям

                if /* Если условие без свойств */ (requiredFeatures.isEmpty()
                        && entities.stream().anyMatch(ent -> ent.name.equals(this.entityName))) {
                    this.score = 1;
                }

                for (Entity iterEntity : entities) {
                    if (iterEntity.name.equals(this.entityName)
                            && requiredFeatures.stream().allMatch(ftr -> ftr.matchesEntity(iterEntity))) {
                        score = 1 + requiredFeatures.size();
                    }
                }
        }
    }

    /**
     * Вес Constraint'а, который был посчитан ранее методом checkEntitiesNremember
     */
    public int getScore() {
        return score;
    }

    enum Operator {
        EXISTS,
        NOT_EXISTS,
        EXISTS_OPTIONALLY
    }

    @Override
    public String toString() {
        return "Constraint{" +
                "name='" + name + '\'' +
                ", entityName='" + entityName + '\'' +
                ", operator=" + operator +
                ", requiredFeatures=" + requiredFeatures +
                ", matched=" + matched +
                ", score=" + score +
                '}';
    }

    class Feature {
        public final String reqFeatureName;
        private final List<String> requiredValues = new LinkedList<>();
        private final List<String> prohibitedValues = new LinkedList<>();

        Feature(String name, String values) {
            this.reqFeatureName = name;
            Arrays.stream(values.split(";"))
                    .map(String::trim)
                    .forEach(this::addValue);
        }

        private void addValue(String value) {
            if (value.startsWith("!") && value.length() > 1) {
                prohibitedValues.add(value.substring(1));
            } else {
                requiredValues.add(value);
            }
        }

        public boolean matchesEntity(Entity checkedEntity) {
            String valueToCheck = checkedEntity.getFeatureValue(reqFeatureName);
            if (valueToCheck == null) {
                // Если проверяемой черты нет, и список запретных значений не пустой - считаем это совпадением.
                // Если список запретов пустой - значит отсутствие черты нам не подходит.
                return !prohibitedValues.isEmpty();
            } else if /* valueToCheck != null */ (
                    !prohibitedValues.isEmpty() && !prohibitedValues.contains(valueToCheck)) {
                // Если список запретов НЕ пустой И значение НЕ в этом списоке - сочитаем совпадением
                return true;
            } else return requiredValues.contains(valueToCheck); // Либо проверяем наличие в списке искомых значений
        }

        @Override
        public String toString() {
            return "Feature{" +
                    "name='" + reqFeatureName + '\'' +
                    ", allowed='" + requiredValues +
                    ", prohibited='" + prohibitedValues + '\'' +
                    '}';
        }
    }
}
