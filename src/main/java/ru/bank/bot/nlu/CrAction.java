package ru.bank.bot.nlu;

import java.util.Map;

/**
 * Context Rule Action
 */
public abstract class CrAction {
    public String contextRule;

    /**
     * Применить конекстное правило
     *
     * @param utterance       Для добавления новой Entity в общий список
     * @param matchedEntities Указатели Entity, которые подпали под паттерн
     */
//    public abstract void apply(Utterance utterance, List<Entity> entities);
    public abstract void apply(Utterance utterance, Map<Integer, Entity> matchedEntities);
}
