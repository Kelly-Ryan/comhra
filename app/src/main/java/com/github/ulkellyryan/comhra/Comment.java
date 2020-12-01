package com.github.ulkellyryan.comhra;

import android.icu.text.DateFormat;

import com.google.firebase.Timestamp;

public class Comment {

    Timestamp timestamp;

    public Comment(){
    }

    public Comment(String user, String text, Timestamp timestamp) {
        this.user = user;
        this.text = text;
        this.timestamp = timestamp;
    }

    String text, user;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate(){
        DateFormat df = DateFormat.getDateTimeInstance();
        return df.format(getTimestamp().toDate());
    }
}
