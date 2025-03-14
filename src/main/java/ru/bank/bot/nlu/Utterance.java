package ru.bank.bot.nlu;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.bot.SqlRequest;
import ru.bank.bot.utils.Utils;

import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static ru.bank.bot.utils.Utils.toDiamantLog;

public class Utterance {
    public final String utterance;
    private final DialogData dialogData;

    /**
     * Список фрагментов фразы клиента (только строки)
     */
    private final List<String> partsString = new LinkedList<>();

    /**
     * Список фрагментов фразы клиента (объекты)
     */
    private final List<Part> parts = new LinkedList<>();
    public final List<Entity> entities = new LinkedList<>();
    public List<String> contexts = new LinkedList<>();
    String fieldToElicit;
    String targetName;
    String actionName;
    /**
     * Список всех интентов-кандидатов и их score. Используется только для тестирования.
     */
    List<Intent> intents = new LinkedList<>();


    /**
     * @param utterance     Фраза клиента
     * @param fieldToElicit Запрошенное поле (для определения контекста)
     * @param targetName    Имя таргета (для определения контекста)
     * @param actionName    Имя действия (для определения контекста)
     * @param dialogData    Для логирования в лог диалога
     */
    public Utterance(String utterance, String fieldToElicit, String targetName, String actionName
            , DialogData dialogData) throws SQLException {
        this.utterance = utterance;
        this.dialogData = dialogData;
        this.fieldToElicit = fieldToElicit;
        this.targetName = targetName;
        this.actionName = actionName;
        splitUtterance(utterance);
        if (parts.isEmpty()) {
            return;
        }
        assignEntitesFromDictionary();
        assignEmbeddedEntities();
        getContexts();
        applyContextRules();
        saveUnrecognizedToDb();
        toDiamantLog(dialogData, "Финальный набор Entity: " + this.entities);
    }

    /**
     * Разделение фразы на фрагменты с присвоением индекса.
     */
    private void splitUtterance(String utterance) {
        toDiamantLog(dialogData, "Фраза клиента: " + utterance);

        // Удаление одиночных кавычек (мешают запросу SQL)
        utterance = utterance.replace("'", "");

        // Поиск гиперссылок
        Pattern pattern = Pattern.compile("https?://[A-z0-9!#$&'()*+,/:;=?@\\[\\]\\-_.~%]+");
        Matcher matcher = pattern.matcher(utterance);
        Set<Integer> notCutBefore = new HashSet<>(); // Не разрезать фразу перед символом
        while (matcher.find()) {
            for (int i = matcher.start() + 1; i < matcher.end(); i++) {
                notCutBefore.add(i);
            }
        }

        // Деление на части
        char[] uttCharArr = utterance.toCharArray();
        CharCollection currPart = new CharCollection();
        for (int i = 0; i < uttCharArr.length; i++) {
            char iterChar = uttCharArr[i];
            CharType iterCharType = getCharType(iterChar);
            if (currPart.isEmpty()) { // Первый символ
                currPart.add(iterChar);
            } else if (notCutBefore.contains(i)) { // Символ нельзя отделять от предыдущего
                currPart.add(iterChar);
            } else if (currPart.lastCharType().get() == iterCharType) { // Тип символа совпадает с предыдущим (сцепляем)
                currPart.add(iterChar);
            } else {
                partsString.add(currPart.getAndErase());
                currPart.add(iterChar);
            }
        }

        // Последний справа фрагмент
        if (currPart.notEmpty()) {
            partsString.add(currPart.getAndErase());
        }

        // Назначение индексов фрагментам и инициализация объектов Part для каждого из них
        for (int i = 0; i < partsString.size(); i++) {
            String coveredText = partsString.get(i);
            parts.add(new Part(i, coveredText, this));
        }

        toDiamantLog(dialogData, "Фраза разделена на фрагменты: " + parts);
    }

    public int partsCount() {
        return parts.size();
    }

    private void assignEntitesFromDictionary() throws SQLException {
        List<Entity> entities = SqlRequest.getDictionaryMatches(this.partsString, this.dialogData);
        for (Part part : parts) {
            for (Entity iterEntity : entities) {
                if (part.textMatchesCiAi(iterEntity.token)) {
                    Entity assignedEntity = iterEntity.clone();
                    assignedEntity.linkToUtterancePart(part);
                    this.entities.add(assignedEntity);
                }
            }
        }
        toDiamantLog(dialogData, "По словарю назначены Entity: " + this.entities);
    }

    /**
     * Получение всех Entity для передачи в запрос интентов
     */
    public List<Entity> getAllEntities() {
        return entities;
    }

    private void applyContextRules() {
        List<String> appliedRules = new LinkedList<>();
        for (ContextRule contextRule : CustomConfig.getContextRules()) {
            contextRule.checkAndApply(this, appliedRules, dialogData);
        }
        toDiamantLog(dialogData, "Сработали контекстные правила: " + appliedRules);
    }

    /**
     * Список сущностей, начинающихся в указанном индексе.
     * Используется при переборе паттернов контекстных правил.
     */
    public List<Entity> getEntitiesBeginWithIndex(int index) {
        List<Entity> output = new LinkedList<>();
        entities.stream()
                .filter(x -> x.getMinIndex() == index)
                .forEach(output::add);
        return output;
    }

    /**
     * Получение соседних справа Entity относительно поднной на вход.
     * Используется при переборе паттернов контекстных правил.
     */
    public Stream<Entity> getRightNeighbors(Entity sourceEntity) {
        int neighborBeginIndex = sourceEntity.getMaxIndex() + 1;
        return entities.stream()
                .filter(entity -> entity.getMinIndex() == neighborBeginIndex);
    }

    /**
     * Запуск NLU-движка и запись в output запрошенных в fieldToElicit полей
     *
     * @param output Выходные поля
     */
    public void runNlu(Map<String, ApiField> output) {
        output.clear(); // Текущий интент уже находится в output, мешает same state: Intent updated. Ignore same state
        // Сброс флагов
        output.put("BEnluFoundIntent", ApiField.builder()
                .name("BEnluFoundIntent")
                .value("false")
                .status(FieldStatus.DEFINED).build());
        output.put("BEnluSameIntent", ApiField.builder()
                .name("BEnluSameIntent")
                .value("false")
                .status(FieldStatus.DEFINED).build());


        if (fieldToElicit.equals("Intent")) { // Если ищется интент
            Optional<Intent> intentOptional = getIntentWithHighestScore();
            if (intentOptional.isPresent()) {
                dialogData.resetNoMatchReaction();
                Intent intent = intentOptional.get();

                output.put("BEnluFoundIntent", ApiField.builder()
                        .name("BEnluFoundIntent")
                        .value("true")
                        .status(FieldStatus.DEFINED).build());

                // Если вернулся тот же интент - не меняем для обхода "Intent updated. Ignore same state."
                /*if (dialogData.isSameIntentReturned(intent.intentName)) {
                    toDiamantLog(dialogData, "Интент повторный - для вызвова Same State поле обновляться не будет.");
                    output.put("BEnluSameIntent",  ApiField.builder()
                            .name("BEnluSameIntent")
                            .value("true")
                            .status(FieldStatus.DEFINED).build());
                } else {
                    output.put("Intent", ApiField.builder()
                            .name("Intent")
                            .value(intent.intentName)
                            .status(intent.getFieldStatus()).build());
                }*/

                output.put("Intent", ApiField.builder()
                        .name("Intent")
                        .value(intent.intentName)
                        .status(intent.getFieldStatus()).build());
            }
        } else { // Если ищется поле НЕ интент
            Optional<Entity> searchedEntity = getAllEntities().stream()
                    .filter(entity -> entity.name.equals(fieldToElicit))
                    .filter(entity -> entity.getFeatureValue("Value") != null)
                    .findFirst();
            if (searchedEntity.isPresent()) {
                dialogData.resetNoMatchReaction();
                output.put(searchedEntity.get().name, ApiField.builder()
                        .name(searchedEntity.get().name)
                        .value(searchedEntity.get().getFeatureValue("Value"))
                        .status(FieldStatus.DEFINED).build()); // INFERRED
                toDiamantLog(dialogData, "Модуль NLU возвращает поля: " + Utils.outputMap2String(output));
            } else {
                toDiamantLog(dialogData, "NLU не нашёл поле: " + fieldToElicit);
            }
        }
    }

    public Optional<Intent> getIntentWithHighestScore() {
        try {
            intents = SqlRequest.getIntentsCandidates(this.getAllEntities());
        } catch (SQLException e) {
            toDiamantLog(dialogData, "Не удалось получить интенты из БД: " + e);
            return Optional.empty();
        }

        Optional<Intent> intent = intents.stream()
                .filter(x -> x.matches(this.getAllEntities()))
                .max(Comparator.comparingInt(Intent::getHighestScore));

        if (intent.isPresent()) {
            toDiamantLog(dialogData, "Определён интент: " + intent.get().intentName
                    + ", статус: " + intent.get().getFieldStatus()
                    + ", score: " + intent.get().getHighestScore() + ".");
            return intent;
        }
        return Optional.empty();
    }

    /**
     * Вывод всех интентов-кандидатов и их score. Используется только для тестирования
     */
    public void printAllIntentsWithScore() {
        if (intents.isEmpty()) {
            return;
        }
        System.out.print("Все интенты (только для тест-сетов): ");

        for (Intent iterIntent : intents) {
            for (Map.Entry<String, Integer> iterConSet : iterIntent.getConstraintSetAndHighestScore().entrySet()) {
                if (iterConSet.getValue() > 0) {
                    System.out.print(iterIntent.intentName + "{ConstraintSet: " + iterConSet.getKey()
                            + ", Score: " + iterConSet.getValue() + "} ");
                }
            }
        }
        System.out.println(); // Для перевода строки
    }

    /**
     * Назначение встроенных сущностей (Number и т.д.)
     */
    private void assignEmbeddedEntities() {
        List<Entity> embeddedEntities = new LinkedList<>(); // Только для вывода результата

        // Number - числа
        for (Part part : parts) {
            if (part.coveredText.matches("\\d+")) {
                Entity numberEntity = new Entity("Number", part.coveredText);
                numberEntity.addFeature("Value", part.coveredText);
                numberEntity.addFeature("Length", String.valueOf(part.coveredText.length()));
                numberEntity.linkToUtterancePart(part);
                entities.add(numberEntity);
                embeddedEntities.add(numberEntity);
            }
        }

        // Unrecognized
        getUnrecognizedParts()
                .forEach(part -> {
                    Entity entity = new Entity("Unrecognized", part);
                    entities.add(entity);
                    embeddedEntities.add(entity);
                });

        // Input
        Entity inputEntity = new Entity("Input", utterance);
        inputEntity.addFeature("Value", utterance);
        parts.forEach(inputEntity::linkToUtterancePart);
        embeddedEntities.add(inputEntity);
        entities.add(inputEntity);

        toDiamantLog(dialogData, "Назначены технические Entity: " + embeddedEntities);
    }

    /**
     * Определяем текущие контексты
     */
    private void getContexts() {
        try {
            contexts = SqlRequest.getContexts(fieldToElicit, targetName, actionName);
        } catch (SQLException e) {
            toDiamantLog(dialogData, "Не удалось получить контексты из БД:", e);
        }

        toDiamantLog(dialogData, "Определены контексты: " + contexts);
    }

    /**
     * Получить фрагменты, которым не назначены Entity
     */
    public Stream<Part> getUnrecognizedParts() {
        Set<Integer> recognizedIndexes = new HashSet<>();
        for (Entity entity : entities) {
            for (int index : entity.getAllIndexes()) {
                recognizedIndexes.add(index);
            }
        }

        return parts.stream().filter(x -> !recognizedIndexes.contains(x.index));
    }

    /**
     * Сохранение в БД токенов Unrecognized, сохранившихся после контекстных правил.
     */
    public void saveUnrecognizedToDb() {
        entities.stream()
                .filter(entity -> entity.name.equals("Unrecognized"))
                .forEach(entity -> entity.getParts().forEach(
                        part -> SqlRequest.saveUnrecognized(part.coveredText)));
    }


    public class Part {
        private final int index;
        private final String coveredText;
        private final Utterance linkToUtterance;

        /**
         * @param index           Индекс фрагмента
         * @param coveredText     Текст фрагмента
         * @param linkToUtterance Ссылка на Utterance
         */
        Part(int index, String coveredText, Utterance linkToUtterance) {
            this.index = index;
            this.coveredText = coveredText;
            this.linkToUtterance = linkToUtterance;
        }

        /**
         * Проверка равенства фрагмента и входящей строки.
         * CI & AI - Case & Accent Insensitive (регистр и е-ё игнорируются).
         */
        public boolean textMatchesCiAi(String input) {
            return coveredText.replaceAll("[Ёё]", "е").equalsIgnoreCase(input);
        }

        /**
         * Получение фрагмента фразы в изначальном виде (оригинальный регистр букв, е-ё сохранены)
         */
        public String getOriginalText() {
            return coveredText;
        }

        public int getIndex() {
            return index;
        }

        /**
         * Является ли фрагмент началом высказывания?
         */
        public boolean isSentenceStart() {
            return index == 0;
        }

        /**
         * Является ли фрагмент концом высказывания?
         */
        public boolean isSentenceEnd() {
            return index == linkToUtterance.parts.size() - 1;
        }

        @Override
        public String toString() {
            return "'" + coveredText + "' {" +
                    "index=" + index +
                    '}';
        }
    }

    /**
     * Тип символа для разделения. Разделение фразы на фрагменты происходит между стоящими рядом символами разных типов.
     */
    private enum CharType {
        DIGIT,
        LETTER,
        SPACE,
        BRACKET_OR_QUOTA,
        QUESTION_OR_EXCLAMATION,
        DOT,
        OTHER_PUNCTUATION,
        OTHER
    }

    /**
     * Определить группу (CharType) символа.
     */
    private CharType getCharType(char x) {
        if (x == ' ' || x == '\n') {
            return CharType.SPACE;
        } else if (x == '.') {
            return CharType.DOT;
        } else if (x == '!' || x == '?') {
            return CharType.QUESTION_OR_EXCLAMATION;
        } else if (x == '"' || x == '(' || x == ')' || x == '«' || x == '»') {
            return CharType.BRACKET_OR_QUOTA;
        } else if (x == '№' || x == '*' || x == '%' || x == '#' || x == '@') {
            return CharType.OTHER_PUNCTUATION;
        } else if (String.valueOf(x).matches("[А-яA-zЁё]")) {
            return CharType.LETTER;
        } else if (String.valueOf(x).matches("[\\d]")) {
            return CharType.DIGIT;
        } else {
            return CharType.OTHER;
        }
    }

    /**
     * Класс для накопления из символов будущих фрагментов фразы
     */
    private class CharCollection {
        private StringBuilder charCollection = new StringBuilder();

        /**
         * Добавить символ к фрагменту. Пробелы и переносы строки игнорируются.
         */
        public void add(char x) {
            if (getCharType(x) != CharType.SPACE) {
                charCollection.append(x);
            }
        }

        /**
         * Получаем накопленный фрагмент и очищаем объект.
         */
        public String getAndErase() {
            String output = charCollection.toString();
            charCollection = new StringBuilder();
            return output;
        }

        /**
         * Возвращает последний символ фрагмента
         */
        public Optional<Character> lastChar() {
            if (notEmpty()) {
                return Optional.of(charCollection.charAt(charCollection.length() - 1));
            }
            return Optional.empty();
        }

        /**
         * Возвращает тип последнего символа фрагмента
         */
        public Optional<CharType> lastCharType() {
            if (lastChar().isPresent()) {
                return Optional.ofNullable(getCharType(lastChar().get()));
            }
            return Optional.empty();
        }

        /**
         * Пустой ли фрагмент?
         */
        public boolean isEmpty() {
            return charCollection.length() == 0;
        }

        /**
         * Фрагмент НЕ пустой?
         */
        public boolean notEmpty() {
            return !isEmpty();
        }

        @Override
        public String toString() {
            return charCollection.toString();
        }
    }

}
