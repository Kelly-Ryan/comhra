package com.github.ulkellyryan.comhra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
    }

    public void submitPost(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        EditText newPost = findViewById(R.id.newPostText);
        Post post = new Post(newPost.getText().toString(), user.getDisplayName(), Timestamp.now());
        db.collection("posts").add(post);

        //go back to DisplayPostsActivity where new post can be seen
        Intent intent = new Intent(this, NewsFeedActivity.class);
        startActivity(intent);
    }

    public void photo(View view){
        Intent intent = new Intent(this, PhotoActivity.class);
        startActivity(intent);
    }
}