package com.group10.photoeditor;

import com.google.gson.Gson;

/**
 * Created by jmaciak on 3/28/18.
 */

public class BuildingStateEvent {
    String building_name;
    String open_time;
    String close_time;

    public BuildingStateEvent(String n, String o, String c) {
        this.building_name = n;
        this.open_time = o;
        this.close_time = c;
    }
    public static String toJsonString(BuildingStateEvent e) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(e);
        return jsonString;
    }
    public static BuildingStateEvent fromJsonString(String json) {
        Gson gson = new Gson();
        BuildingStateEvent event = gson.fromJson(json, BuildingStateEvent.class);
        return event;
    }
}
