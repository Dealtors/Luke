package com.example.luke;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherService {
    public interface Callback {
        void onSuccess(WeatherInfo weatherInfo);
        void onError(Exception error);
    }

    private static final WeatherService INSTANCE = new WeatherService();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static WeatherService getInstance() {
        return INSTANCE;
    }

    public void loadWeather(String city, Callback callback) {
        double[] coords = GeoUtils.cityCenter(city);
        String requestUrl = String.format(Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true&timezone=auto",
                coords[0], coords[1]);

        executor.execute(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(requestUrl).openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                StringBuilder builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                }

                JSONObject root = new JSONObject(builder.toString());
                JSONObject current = root.getJSONObject("current_weather");
                double temperature = current.getDouble("temperature");
                double windSpeed = current.getDouble("windspeed");
                int weatherCode = current.getInt("weathercode");
                String time = current.optString("time", "");
                WeatherInfo weatherInfo = new WeatherInfo(city, temperature, windSpeed, weatherCode, time, mapWeatherCode(weatherCode));

                mainHandler.post(() -> callback.onSuccess(weatherInfo));
            } catch (Exception e) {
                Log.e("WEATHER", "Ошибка: " + e.getMessage());
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    private String mapWeatherCode(int code) {
        switch (code) {
            case 0:
                return "ясно";
            case 1:
            case 2:
            case 3:
                return "облачно";
            case 45:
            case 48:
                return "туман";
            case 51:
            case 53:
            case 55:
            case 56:
            case 57:
                return "морось";
            case 61:
            case 63:
            case 65:
            case 66:
            case 67:
                return "дождь";
            case 71:
            case 73:
            case 75:
            case 77:
                return "снег";
            case 80:
            case 81:
            case 82:
                return "ливень";
            case 95:
            case 96:
            case 99:
                return "гроза";
            default:
                return "погода обновлена";
        }
    }
}
