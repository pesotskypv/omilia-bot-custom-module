package ru.bank.bot.nlu;

import java.util.*;

public class Entity {
    public final String name;
    public final String token;
    public final Map<String, String> features;
    private final LinkedHashSet<Utterance.Part> parts = new LinkedHashSet<>();

    public Entity(String name) {
        this.name = name;
        token = null;
        features = new HashMap<>();
    }

    public Entity(String name, String token) {
        this.name = name;
        this.token = token;
        features = new HashMap<>();
    }

    /**
     * Конструктор с линкованием сразу к одному фрагменту
     *
     * @param name Название Entity
     * @param part Фрагмент фразы
     */
    public Entity(String name, Utterance.Part part) {
        this.name = name;
        this.token = null;
        features = new HashMap<>();
        linkToUtterancePart(part);
    }

    /**
     * Конструктор для клонов
     */
    public Entity(String name, String token, Map<String, String> features, LinkedHashSet<Utterance.Part> parts) {
        this.name = name;
        this.token = token;
        this.features = features;
        parts.forEach(this::linkToUtterancePart);
    }

    public void addFeature(String feature, String value) {
        // Пропускаем поля, которые не являются свойствами
        List<String> ignoreNotFeatures = Arrays.asList("Dictionary", "Token", "Entity", "Link2Dictionary", "rowid");
        if (!ignoreNotFeatures.contains(feature) && feature != null && value != null) {
            features.put(feature, value);
        }
    }

    /**
     * Получить значение свойства Entity
     *
     * @param featureName Имя свойства
     */
    public String getFeatureValue(String featureName) {
        if (features.containsKey(featureName)) {
            return features.get(featureName);
        }
        return null;
    }

    public void linkToUtterancePart(Utterance.Part part) {
        parts.add(part);
        if (part.isSentenceStart()) {
            addFeature("SentenceStart", "true");
        }
        if (part.isSentenceEnd()) {
            addFeature("SentenceEnd", "true");
        }
    }

    /**
     * Проверка, занимает ли сущность указанный индекс.
     */
    public boolean includesIndex(int index) {
        return parts.stream()
                .anyMatch(x -> x.getIndex() == index);
    }

    public int getMaxIndex() {
        return parts.stream()
                .max(Comparator.comparingInt(Utterance.Part::getIndex))
                .get()
                .getIndex();
    }

    public int getMinIndex() {
        return parts.stream()
                .min(Comparator.comparingInt(Utterance.Part::getIndex))
                .get()
                .getIndex();
    }

    public int[] getAllIndexes() {
        return parts.stream()
                .mapToInt(Utterance.Part::getIndex)
                .toArray();
    }

    public String getCoveredText() {
        StringBuilder output = new StringBuilder();
        parts.stream()
                .sorted(Comparator.comparingInt(Utterance.Part::getIndex))
                .forEach(part -> output.append(part.getOriginalText()).append("\t"));
        return output.toString().replaceAll("\t$", "");
    }

    /**
     * Задать новый набор фрагментов фразы, к которым прилинкована сущность.
     * Используется в Change Span действиях контекстных правил.
     */
    public void setNewSpan(LinkedHashSet<Utterance.Part> newSpan) {
        parts.clear();
        newSpan.forEach(this::linkToUtterancePart);
    }

    public Set<Utterance.Part> getParts() {
        return parts;
    }

    public Entity clone() {
        return new Entity(this.name
                , this.token
                , new HashMap<>(this.features)
                , new LinkedHashSet<>(this.parts));
    }

    @Override
    public String toString() {
        return name + "{" +
                "features=" + features +
                ", indexes=" + Arrays.toString(getAllIndexes()) +
                '}';
    }
}
