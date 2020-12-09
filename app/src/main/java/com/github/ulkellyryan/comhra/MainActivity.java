package com.github.ulkellyryan.comhra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final Uri defaultProfilePhoto = Uri.parse("https://firebasestorage.googleapis.com/v0/b/comhra-35d60.appspot.com/o/pictures%2Fdefault-avatar.png?alt=media&token=912372e4-9dca-4f78-be13-d28dda1577ba");
    private final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickLogIn(View view){
        //Email is chosen authentication method
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setTheme(R.style.OrangeTheme).build(), RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK){
                addNewUser();
                displayNewsFeed();
            } else {
                if (response == null) {
                    System.out.println("Sign in cancelled");
                }
                if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    System.out.println("No internet connection");
                }
            }
        }
    }

    public void displayNewsFeed(){
        Intent intent = new Intent(this, NewsFeedActivity.class);
        startActivity(intent);
    }

    public void addNewUser(){
        //On a successful login/registration check whether the user is already in the Users collection. If not, add them to the users collection.
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        assert fbuser != null;

        DocumentReference docRef = firestore.collection("users").document(fbuser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d("TAG", "User already exists in Users collection. DocumentSnapshot data: " + document.getData());
                    } else {
                        //set default profile photo if none exists
                        if(fbuser.getPhotoUrl() == null){
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(defaultProfilePhoto)
                                    .build();

                            fbuser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG", "Default profile photo set.");
                                            }
                                        }
                                    });
                        }

                        firestore.collection("users").document(fbuser.getUid()).set(new User(fbuser.getDisplayName(), fbuser.getEmail(), defaultProfilePhoto.toString(), fbuser.getUid()));
                        Log.d("TAG", "User added to Users collection and default profile photo set.");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }
}