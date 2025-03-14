package ru.bank.bot.doatm;

import java.util.Objects;

public class Atm extends Servicepoint {
    public final String bank;
    private final String schedule;

    public Atm(String id, double lat, double lon, int distance, String address, String bank, String schedule) {
        super(id, lat, lon, distance, address);
        this.bank = bank;
        this.schedule = schedule;
    }

    /**
     * Для исключения дублей по имени в HashSet.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        else if (o == null || getClass() != o.getClass()) return false;
        else {
            Atm atm2 = (Atm) o;
            return this.getRoundedDistance().equals(atm2.getRoundedDistance())
                    && this.address.equals(atm2.address)
                    && this.bank.equals(atm2.bank);
        }
    }

    /**
     * Для исключения дублей по имени в HashSet.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.getRoundedDistance(), address, bank);
    }

    @Override
    public String getSchedule() {
        if (schedule != null && schedule.length() > 1) {
            return schedule;
        } else {
            return null;
        }
    }
}
