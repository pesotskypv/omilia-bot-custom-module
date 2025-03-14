package ru.bank.bot.doatm;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Office extends Servicepoint {
    private final JsonArray schedule;

    public Office(String id, double lat, double lon, int distance, String address, JsonArray schedule) {
        super(id, lat, lon, distance, address);
        this.schedule = schedule;
    }

    @Override
    public String getSchedule() {
        Map<String, String> resultschedule = new LinkedHashMap<>();

        // 1. Все дни, которых не будет в расписании - выходные, поэтому делаем все выходными по умолчанию.
        resultschedule.put("пн", "выходной");
        resultschedule.put("вт", "выходной");
        resultschedule.put("ср", "выходной");
        resultschedule.put("чт", "выходной");
        resultschedule.put("пт", "выходной");
        resultschedule.put("сб", "выходной");
        resultschedule.put("вс", "выходной");


        // 2. Обходим расписание из API. По тем дням, которые есть, обновляем расписание в resultschedule.
        for (JsonElement JEday : schedule) {
            JsonObject JOday = JEday.getAsJsonObject();
            switch (JOday.get("dayOfWeek").getAsString()) {
                case "MONDAY":
                    resultschedule.put("пн", JOday.get("workStart").getAsString() + "-" + JOday.get("workEnd").getAsString());
                    break;
                case "TUESDAY":
                    resultschedule.put("вт", JOday.get("workStart").getAsString() + "-" + JOday.get("workEnd").getAsString());
                    break;
                case "WEDNESDAY":
                    resultschedule.put("ср", JOday.get("workStart").getAsString() + "-" + JOday.get("workEnd").getAsString());
                    break;
                case "THURSDAY":
                    resultschedule.put("чт", JOday.get("workStart").getAsString() + "-" + JOday.get("workEnd").getAsString());
                    break;
                case "FRIDAY":
                    resultschedule.put("пт", JOday.get("workStart").getAsString() + "-" + JOday.get("workEnd").getAsString());
                    break;
                case "SATURDAY":
                    resultschedule.put("сб", JOday.get("workStart").getAsString() + "-" + JOday.get("workEnd").getAsString());
                    break;
                case "SUNDAY":
                    resultschedule.put("вс", JOday.get("workStart").getAsString() + "-" + JOday.get("workEnd").getAsString());
                    break;
            }
        }

        // 3. Формируем расписание в строке, схлопываем дни с одинаковым расписанием в диапазоны.
        String result = "";
        String fstday = null; // первый день в диапазоне
        String lastday = null; // последний день в диапазоне
        String time = null; // время
        for (Map.Entry<String, String> entry : resultschedule.entrySet()) {
            if (fstday == null) { // первый день в массиве
                fstday = entry.getKey();
                time = entry.getValue();
            } else if (entry.getValue().equals(time)) { // если расписание текущего дня совпадает с предыдущим
                lastday = entry.getKey();
            } else if (lastday != null) { // если расписание не совпадает и накоплено более 1 дня
                result = result + fstday + "-" + lastday + ": " + time + "\n";
                fstday = entry.getKey();
                time = entry.getValue();
                lastday = null;
            } else { // если расписание не совпадает и накоплен только 1 день
                result = result + fstday + ": " + time + "\n";
                fstday = entry.getKey();
                time = entry.getValue();
            }
        }

        if (lastday != null) { // если остался накопленный диапазон
            result = result + fstday + "-" + lastday + ": " + time;
        } else if (fstday != null) { // если остался накопленный день
            result = result + fstday + ": " + time;
        }

        return result;
    }
}
