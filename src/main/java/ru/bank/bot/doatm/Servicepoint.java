package ru.bank.bot.doatm;

public abstract class Servicepoint {
    public final String id;
    public final double lat;
    public final double lon;
    private final int distance;
    public final String address;

    protected Servicepoint(String id, double lat, double lon, int distance, String address) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.distance = distance;
        this.address = address;
    }


    public String getRoundedDistance() {
        if (distance <= 50) {
            return ((distance + 4) / 5 * 5) + " м"; // округляем до 5 м в большую сторону
        } else if (distance <= 400) {
            return ((distance + 9) / 10 * 10) + " м"; // до 10 м
        } else if (distance <= 1000) {
            return ((distance + 49) / 50 * 50) + " м"; // до 50 м
        } else if (distance <= 5000) {
            return (float) (((distance + 99) / 100 * 100)) / 1000 + " км"; // до 0.1 км
        } else {
            return (float) (((distance + 499) / 500 * 500)) / 1000 + " км"; // до 0.5 км
        }
    }

    public static String clearAddress(String address) {
        return address
                .replaceAll("^Россия, ", "")
                .replace("г Москва,", "Москва,")
                .replaceAll("[Гг]ород Москва,", "Москва,")
                .replace("Москва, Москва,", "Москва,")
                // далее - технические символы
                .replaceAll(",+", ",") // Повторные запятые
                .replace(", ,", ",") // Повторные запятые c пробелом
                .replace("#", " ") // Omilia не поддерживает решётки
                .replaceAll("\\s+", " "); // Лишние пробелы
    }

    public String getClearAddress() {
        return clearAddress(address);
    }

    public abstract String getSchedule();

    @Override
    public String toString() {
        return "Servicepoint{" +
                "id='" + id + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", distance=" + distance +
                ", address='" + address + '\'' +
                '}';
    }
}
