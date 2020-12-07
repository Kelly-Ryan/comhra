package com.github.ulkellyryan.comhra;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class User {

    String name;
    String email;
    String uid;
    String profilePhotoUri;

    Uri defaultProfilePhoto = Uri.parse("https://firebasestorage.googleapis.com/v0/b/comhra-35d60.appspot.com/o/pictures%2Fdefault-avatar.png?alt=media&token=912372e4-9dca-4f78-be13-d28dda1577ba");

    FirebaseUser fbuser;

    public User(String name, String email, String uid){
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        fbuser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User profile updated.");
                        }
                    }
                });
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        fbuser.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User email address updated.");
                        }
                    }
                });
    }

    public String getUid(){
        return uid;
    }

    public Uri getProfilePhotoUri() {
        return fbuser.getPhotoUrl();
    }

    public void setProfilePhotoUri(Uri profilePhotoUri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
            .setPhotoUri(profilePhotoUri)
            .build();

        fbuser.updateProfile(profileUpdates)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "User profile updated.");
                    }
                }
            });
    }

}
