package ru.bank.aid.service;

import redis.clients.jedis.Jedis;
import ru.bank.bot.DialogData;

import java.util.TimeZone;

import static ru.bank.util.Utils.*;

public class AidService {
    public static void getTimeZoneByPhone(String phone, DialogData dialogData) {
        genericLogInfo("Выполняется getTimeZoneByPhone: " + phone);
        if (phone == null) {
            return;
        }

        String timeZoneId;
        int pl = phone.length();

        if (pl == 0) {
            dialogLogWarn(dialogData, "Часовой пояс по мобильному номеру не определён.");
            return;
        }
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            timeZoneId = jedis.get(phone);
        }
        if (timeZoneId != null) {
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

            if (timeZone != null) {
                dialogLogInfo(dialogData, "По мобильному номеру: " + phone + " определён часовой пояс: "
                        + timeZone.getID());
            } else {
                dialogLogWarn(dialogData, "По мобильному номеру: " + phone + " часовой пояс не определён.");
            }
        } else {
            getTimeZoneByPhone(phone.substring(0, pl - 1), dialogData);
        }
    }
}
