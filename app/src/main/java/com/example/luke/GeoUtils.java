package com.example.luke;

import java.util.HashMap;
import java.util.Map;

public final class GeoUtils {
    private static final Map<String, double[]> CITY_COORDS = new HashMap<>();

    static {
        CITY_COORDS.put("Красноярск", new double[]{56.0153, 92.8932});
        CITY_COORDS.put("Москва", new double[]{55.7558, 37.6173});
        CITY_COORDS.put("Санкт-Петербург", new double[]{59.9386, 30.3141});
        CITY_COORDS.put("Новосибирск", new double[]{55.0084, 82.9357});
        CITY_COORDS.put("Екатеринбург", new double[]{56.8389, 60.6057});
    }

    private GeoUtils() {}

    public static double[] cityCenter(String city) {
        double[] coords = CITY_COORDS.get(city);
        if (coords == null) {
            coords = CITY_COORDS.get("Красноярск");
        }
        return new double[]{coords[0], coords[1]};
    }

    public static double[] coordinatesForPoint(String city, String address) {
        double[] base = cityCenter(city);
        int hash = address == null ? 0 : Math.abs(address.hashCode());
        double latOffset = ((hash % 1000) - 500) / 100000.0;
        double lonOffset = (((hash / 1000) % 1000) - 500) / 100000.0;
        return new double[]{base[0] + latOffset, base[1] + lonOffset};
    }
}
