package ru.bank.bot.nlu;

public class DictionaryItem {
    public final String dictName;
    public final String dictValue;

    public DictionaryItem(String dictName, String dictValue) {
        this.dictName = dictName;
        this.dictValue = dictValue;
    }

    @Override
    public String toString() {
        return "{" +
                "dictName='" + dictName + '\'' +
                ", dictValue='" + dictValue + '\'' +
                '}';
    }
}
