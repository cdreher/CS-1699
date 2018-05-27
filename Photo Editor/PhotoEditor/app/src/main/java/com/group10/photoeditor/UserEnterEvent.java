package com.group10.photoeditor;

import com.google.gson.Gson;

/**
 * Created by jmaciak on 3/28/18.
 */

public class UserEnterEvent {
    String timestamp;
    String building_name;

    public UserEnterEvent(String t, String b) {
        this.timestamp = t;
        this.building_name = b;
    }

    public static String toJsonString(UserEnterEvent e) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(e);
        return jsonString;
    }

    public static UserEnterEvent fromJsonString(String json) {
        Gson gson = new Gson();
        UserEnterEvent event = gson.fromJson(json, UserEnterEvent.class);
        return event;
    }

}
