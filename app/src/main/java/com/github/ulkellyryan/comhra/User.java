package com.github.ulkellyryan.comhra;

import android.net.Uri;

public class User {

    String name;
    String email;
    String pronouns;
    String bio;
    String location;
    String uid;
    String photoUri;

    public User(){

    }

    public User(String name, String email, String photoUri, String uid){
        this.name = name;
        this.email = email;
        this.photoUri = photoUri;
        this.uid = uid;
        pronouns = "";
        bio = "";
        location = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
