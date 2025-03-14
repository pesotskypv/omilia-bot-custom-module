package ru.bank.bot;

import java.util.LinkedHashSet;
import java.util.Set;

public class CalculateGeo {
    public static int distance(double lat1, double lat2, double lon1, double lon2) {
        // The math module contains a function named toRadians which converts from degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth 6371 kilometers
        int r = 6371000; //meters

        // calculate the result
        return (int) (c * r);
    }

    public static String roundDistance(int distance) {
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

    public static void printAllDistancePrompts(int maxdistance) {
        Set<String> result = new LinkedHashSet<>();
        for (int i = 0; i != maxdistance; i++) {
            result.add(roundDistance(i));
        }

        System.out.println(result);
    }
}