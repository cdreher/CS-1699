package com.group10.photoeditor; /**
 * Created by saiko on 4/1/2018.
 */
import com.google.gson.Gson;

public class SportEvent {
    String sport;
    String opponent_name;
    String result;
    String score;
    String opponent_score;

    public SportEvent(String sport, String opponent_name, String result, String score, String opponent_score) {
        this.sport = sport;
        this.opponent_name = opponent_name;
        this.result        = result;
        this.score         = score;
        this.opponent_score = opponent_score;
    }

    public static String toJsonString(SportEvent e) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(e);
        return jsonString;
    }

    public static SportEvent fromJsonString(String json) {
        Gson gson = new Gson();
        SportEvent event = gson.fromJson(json, SportEvent.class);
        return event;
    }
}
