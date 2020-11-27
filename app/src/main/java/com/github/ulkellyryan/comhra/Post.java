package com.github.ulkellyryan.comhra;

import android.icu.text.DateFormat;
import android.net.Uri;
import android.widget.ImageView;

import com.google.firebase.Timestamp;

public class Post {

    String text, user;
    Timestamp timestamp;
    String imageUri;

    public Post(){
    }

    public Post(String text, String user, com.google.firebase.Timestamp timestamp){
        this.text = text;
        this.user = user;
        this.timestamp = timestamp;
    }

    public Post(String text, String uri, String user, com.google.firebase.Timestamp timestamp){
        this.text = text;
        this.imageUri = uri;
        this.user = user;
        this.timestamp = timestamp;
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

    public String getImageUri(){
        return imageUri;
    }

    public String toString(){
        DateFormat df = DateFormat.getDateTimeInstance();
        String date = df.format(getTimestamp().toDate());

        if(imageUri != null){
            return "Post: " + getText() + "\n" + "Image uri:" + getImageUri() + "\n" + "User: " + getUser() + "\n" + date;
        } else {
            return "Post: " + getText() + "\n" + "User: " + getUser() + "\n" + date;
        }
    }
}
