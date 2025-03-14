package ru.bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.bank.bot.service.BotService.toCurrencySymbol;

public class TestToCurrencySymbol {

    @Test
    public void USD() {
        String input = "USD";
        String output = toCurrencySymbol(input);
        System.out.println("input: " + input + "; output: " + output);
        assertEquals("$", output);
    }

    @Test
    public void EUR() {
        String input = "EUR";
        String output = toCurrencySymbol(input);
        System.out.println("input: " + input + "; output: " + output);
        assertEquals("€", output);
    }

    @Test
    public void RUB() {
        String input = "RUB";
        String output = toCurrencySymbol(input);
        System.out.println("input: " + input + "; output: " + output);
        assertEquals("₽", output);
    }

    @Test
    public void rub() {
        String input = "rub";
        String output = toCurrencySymbol(input);
        System.out.println("input: " + input + "; output: " + output);
        assertEquals("₽", output);
    }

    @Test
    public void otherCurrencies() {
        String input = "Abc";
        String output = toCurrencySymbol(input);
        System.out.println("input: " + input + "; output: " + output);
        assertEquals("ABC", output);
    }
}
