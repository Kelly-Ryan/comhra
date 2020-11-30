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

    public String getDate(){
        DateFormat df = DateFormat.getDateTimeInstance();
        return df.format(getTimestamp().toDate());
    }

    public String getImageUri(){
        return imageUri;
    }

    public String toString(){
        if(imageUri != null){
            return "Post: " + getText() + "\n" + "Image uri:" + getImageUri() + "\n" + "User: " + getUser() + "\n" + getDate();
        } else {
            return "Post: " + getText() + "\n" + "User: " + getUser() + "\n" + getDate();
        }
    }
}
