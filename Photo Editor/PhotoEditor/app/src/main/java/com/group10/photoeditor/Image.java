package com.group10.photoeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrixColorFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Image implements Serializable {
    private transient Bitmap bitmap;
    private String name;
    private String location;
    private String tag;
    private String sportingEvent;
    private int homeScore;
    private int awayScore;
    private int filter;
    private String opponent;

    public Image(Bitmap bitmap, String name, String location, String tag, String sportingEvent, int homeScore, int awayScore) {
        this.bitmap = bitmap;
        this.name = name;
        this.location = location;
        this.tag = tag;
        this.sportingEvent = sportingEvent;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getTag() {
        return tag;
    }

    public String getSportingEvent() {
        return sportingEvent;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setOpponent(String opponent) { this.opponent = opponent; }

    public String getOpponent() { return opponent; }

    public int getFilter() { return filter; }

    public void setFilter(int filter) { this.filter = filter; }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        if (bitmap != null) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            boolean success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            if (success)
                oos.writeObject(byteStream.toByteArray());
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        byte[] image = (byte[]) ois.readObject();
        if (image != null && image.length > 0)
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}