package com.github.ulkellyryan.comhra;

import android.icu.text.DateFormat;
import com.google.firebase.Timestamp;

public class Post {

    String text, user, uid;
    Timestamp timestamp;
    String imageUri;


    public Post(){
    }

    public Post(String text, String user, String uid, Timestamp timestamp){
        this.text = text;
        this.user = user;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public Post(String text, String uri, String user, String uid, Timestamp timestamp){
        this.text = text;
        this.imageUri = uri;
        this.user = user;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getUid(){
        return uid;
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
