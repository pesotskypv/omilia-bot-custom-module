package ru.bank;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Test;
import ru.bank.bsp.card.dto.ChangeCardStatusRequest;
import ru.bank.bsp.card.model.ChangeStatus;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static ru.bank.bsp.card.service.CardService.getCardPanLast4Dig;
import static ru.bank.util.Utils.phoneTo10Dig;

public class TestFeatures {
    BotModule botModule = new BotModule();

    @Test
    public void testChatButtonDownload() {
        String link = "https://evo.bank.ru/file-upload/?elib_id=51399424";
        String link2 = link.substring(link.length() - 8);

        System.out.println("Link1: " + "ChatButtonDownload(" + link + ")");
        System.out.println("Link2: " + "ChatButtonDownload(" + link2 + ")");

        JsonObject req = new JsonObject();
        req.addProperty("status", "BLOCK");
        System.out.println(req);

        String gson = new Gson().toJson(ChangeCardStatusRequest.builder().status(ChangeStatus.BLOCK).build());
        System.out.println(gson);

        StringEntity entity = new StringEntity((new Gson()).toJson(ChangeCardStatusRequest.builder()
                .status(ChangeStatus.BLOCK).build()));
        System.out.println(entity);

        String cardName = "Мир *2916";
        System.out.println(cardName + ": " + cardName.matches(".*\\d{4}$"));
        System.out.println(cardName.substring(cardName.length() - 4));

        String cardName2 = "9809";
        System.out.println(cardName2 + ": " + cardName.matches(".*\\d{4}$"));
        System.out.println(cardName2.length());

        AtomicReference<String> cardId = new AtomicReference<>();
        if (cardId.get() == null) {
            System.out.println("cardId = null");
        }
        cardId.set(null);
        if (cardId.get() == null) {
            System.out.println("cardId2 = null");
        }
    }

    // EXTERNAL_CURRENCY_TRANSFER -> externalCurrencyTransfers
    @Test
    public void testToCamelCase() {
        StringBuilder camel = new StringBuilder();
        String type = "CASH_WITHDRAWAL_BANK_OFFICE_BY_ACCOUNT".toLowerCase();

        IntStream.range(0, type.length()).forEach(i -> {
            if (i != 0 && type.charAt(i - 1) == '_') {
                camel.append(type.substring(i, i + 1).toUpperCase());
            } else {
                camel.append(type.charAt(i));
            }
        });
        System.out.println("camel: " + camel.toString().replace("_", ""));
    }

    @Test
    public void testStringDate() {
        String oldDate = "2024-07-03T00:00:00.000";
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Format newDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;

        try {
            date = oldDateFormat.parse(oldDate);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        String newDate = newDateFormat.format(date);

        System.out.println("oldDate: " + oldDate);
        System.out.println("newDate: " + newDate);
    }

    @Test
    public void testTimeZone() {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
        System.out.println("timeZone: " + timeZone);
        System.out.println("toZoneId: " + timeZone.toZoneId());
        System.out.println("getID: " + timeZone.getID());
        System.out.println("getDSTSavings: " + timeZone.getDSTSavings());
        System.out.println("getDSTSavings: " + timeZone.getDisplayName());
        System.out.println("getDSTSavings: " + timeZone.getRawOffset());
        System.out.println(phoneTo10Dig("+79857770063"));
    }

    @Test
    public void testGetCardPanLast4Dig() {
        System.out.println(getCardPanLast4Dig("Текущий счёт *2275"));
    }
}
