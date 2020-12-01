package com.github.ulkellyryan.comhra;

import android.icu.text.DateFormat;
import com.google.firebase.Timestamp;

public class Post {

    String text, user;
    Timestamp timestamp;
    String imageUri;

    public Post(){
    }

    public Post(String text, String user, Timestamp timestamp){
        this.text = text;
        this.user = user;
        this.timestamp = timestamp;
    }

    public Post(String text, String uri, String user, Timestamp timestamp){
        this.text = text;
        this.imageUri = uri;
        this.user = user;
        this.timestamp = timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getDate(){
        DateFormat df = DateFormat.getDateTimeInstance();
        return df.format(getTimestamp().toDate());
    }

    public String getImageUri(){
        return imageUri;
    }
}
