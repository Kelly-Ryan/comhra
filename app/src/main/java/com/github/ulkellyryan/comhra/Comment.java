package com.github.ulkellyryan.comhra;

import android.icu.text.DateFormat;

import com.google.firebase.Timestamp;

public class Comment {

    String text, user, profilePhoto, uid;
    Timestamp timestamp;

    public Comment(){
    }

    public Comment(String user, String text, String profilePhoto, String uid, Timestamp timestamp) {
        this.user = user;
        this.text = text;
        this.profilePhoto = profilePhoto;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getProfilePhoto(){
        return profilePhoto;
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
}
