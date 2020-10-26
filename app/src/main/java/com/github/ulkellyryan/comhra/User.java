package com.github.ulkellyryan.comhra;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {

    public String getUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.toString();
    }
}
