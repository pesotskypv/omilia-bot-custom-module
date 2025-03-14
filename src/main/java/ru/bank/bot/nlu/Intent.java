package ru.bank.bot.nlu;

import com.omilia.diamant.dialog.components.fields.FieldStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Intent {
    public final String intentName;
    private List<Constraint> constraints;
    public final boolean isAmbiguous;

    public Intent(String intentName, boolean isAmbiguous) {
        this.intentName = intentName;
        this.constraints = new LinkedList<>();
        this.isAmbiguous = isAmbiguous;
    }

    /**
     * Линкование Constraint'а к Интенту
     */
    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }

    /**
     * Получение списка Constraint Set'ов.
     * Используется при расчёте суммарного score для каждой группы.
     */
    private Set<String> getConstraintGroups() {
        Set<String> output = new HashSet<>();
        constraints.forEach(x -> output.add(x.name));
        return output;
    }

    /**
     * Проверяем набор Entity на соответствие Constraint'ам, и если все Constraint'ы хоть одного Constraint Set'а
     * совпали - возвращаем true.
     * При обходе Constraint'ов результат каждого запоминается.
     */
    public boolean matches(List<Entity> entities) {
        // Проверяем набор Entity на соответстве всем Constraint'ам
        for (Constraint constraint : constraints) {
            constraint.checkEntitiesNremember(entities);
        }

        // Если все Constraint'ы хоть одного Set'а промаркированы "matched" - значит интент найден
        for (String group : getConstraintGroups()) {
            boolean groupMatched = constraints.stream()
                    .filter(x -> x.name.equals(group))
                    .allMatch(x -> x.matched);
            if (groupMatched) {
                return true;
            }
        }
        return false;
    }

    /**
     * Вернуть самый высокий Score среди Costraint Set'ов
     */
    public int getHighestScore() {
        return getConstraintGroups().stream()
                .mapToInt(this::getConstraintSetScore)
                .max()
                .orElse(0);
    }

    /**
     * Получить Constraint Set с наивысшим score.
     *
     * @return {@link Map}&lt;{@link String }, {@link Integer}&gt;, где String - Имя Constraint Set'а, Integer - Score
     */
    public Map<String, Integer> getConstraintSetAndHighestScore() {
        Map<String, Integer> conSetAndScore = new HashMap<>();
        String outputConSet = null;
        Integer outputMaxScore = null;
        for (String iterConstraintSet : getConstraintGroups()) {
            int interMaxScore = getConstraintSetScore(iterConstraintSet);
            if (outputMaxScore == null || interMaxScore > outputMaxScore) {
                outputConSet = iterConstraintSet;
                outputMaxScore = getConstraintSetScore(iterConstraintSet);
            }
        }
        conSetAndScore.put(outputConSet, outputMaxScore);
        return conSetAndScore;
    }

    /**
     * Получение score для Constraint Set
     */
    public int getConstraintSetScore(String constraintSet) {
        AtomicInteger score = new AtomicInteger();
        if (constraints.stream()
                .filter(constraint -> constraint.name.equals(constraintSet))
                .peek(constraint -> score.addAndGet(constraint.getScore()))
                .allMatch(constraint -> constraint.matched)) {
            return score.get();
        }
        return 0;
    }

    public FieldStatus getFieldStatus() {
        if (isAmbiguous) {
            return FieldStatus.AMBIGUOUS;
        }
        return FieldStatus.DEFINED;
    }

    @Override
    public String toString() {
        return "Intent{" +
                "intentName='" + intentName + '\'' +
                '}';
    }
}
