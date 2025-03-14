package ru.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.bank.bot.utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.bank.bot.utils.Utils.clearPhone;

public class TestClearPhone {
    @Test
    public void letters() {
        String input = "aaa";
        System.out.println("input: "+ input);
        String result = null;
        try { result = clearPhone(input); }
        catch (Utils.PhoneShorterThan10Exception e) {
            System.out.println(e);
        }
        assertNull(result);
    }

    @Test
    public void digits10() {
        String input = "1234567890";
        System.out.println("input: "+ input);
        String result = null;
        try { result = clearPhone(input); }
        catch (Utils.PhoneShorterThan10Exception e) {
            System.out.println(e);
        }
        System.out.println("result: " + result);
        assertEquals(result, input);
    }

    @Test
    public void digitsMoreThan10() {
        String input = "12332312321234567890";
        System.out.println("input: "+ input);
        String result = null;
        try { result = clearPhone(input); }
        catch (Utils.PhoneShorterThan10Exception e) {
            System.out.println(e);
        }
        System.out.println("result: " + result);
        assertEquals(result, input.substring(input.length()-10));
    }

    @Test
    /**
     * Here are the common characters which need to be escaped in XML, starting with double quotes:
     *
     * double quotes (") are escaped to &quot;
     * ampersand (&) is escaped to &amp;
     * single quotes (') are escaped to &apos;
     * less than (<) is escaped to &lt;
     * greater than (>) is escaped to &gt;
     */
    public void clearXmlSpecChars() {
        String test_string = "a[a]a&a'a\"";
        String expected_string = "aaaaa";
        System.out.println("Test string: " + test_string);
        String cleared_string = Utils.clearXmlSpecChars(test_string);
        System.out.println("Cleared string: " + cleared_string);
        Assertions.assertEquals(expected_string, cleared_string);
    }
}
