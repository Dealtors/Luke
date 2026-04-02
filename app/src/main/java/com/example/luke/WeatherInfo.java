package com.example.luke;

public class WeatherInfo {
    private final String city;
    private final double temperature;
    private final double windSpeed;
    private final int weatherCode;
    private final String time;
    private final String description;

    public WeatherInfo(String city, double temperature, double windSpeed, int weatherCode, String time, String description) {
        this.city = city;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.weatherCode = weatherCode;
        this.time = time;
        this.description = description;
    }

    public String getCity() { return city; }
    public double getTemperature() { return temperature; }
    public double getWindSpeed() { return windSpeed; }
    public int getWeatherCode() { return weatherCode; }
    public String getTime() { return time; }
    public String getDescription() { return description; }

    public String toDisplayText() {
        return city + ": " + temperature + "°C, " + description + ", ветер " + windSpeed + " м/с";
    }
}
