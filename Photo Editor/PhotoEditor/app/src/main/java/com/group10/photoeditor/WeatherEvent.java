package com.group10.photoeditor;

import com.google.gson.Gson;

/**
 * Created by jmaciak on 3/28/18.
 */

public class WeatherEvent {
    String temperature;
    String weather;

    public WeatherEvent(String t, String w) {
        this.temperature = t;
        this.weather = w;
    }
    public static String toJsonString(WeatherEvent e) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(e);
        return jsonString;
    }
    public static WeatherEvent fromJsonString(String json) {
        Gson gson = new Gson();
        WeatherEvent event = gson.fromJson(json, WeatherEvent.class);
        return event;
    }
}
