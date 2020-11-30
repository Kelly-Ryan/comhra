package com.github.ulkellyryan.comhra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;


public class PostDetailActivity extends AppCompatActivity{

    public static final String KEY_POST_ID = "key_post_id";

    private TextView tvPosterName;
    private TextView tvPostText;
    private TextView tvDate;
    private ImageView ivPhoto;

    private FirebaseFirestore firestore;
    private DocumentReference postRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        tvPosterName = findViewById(R.id.posterNameTextView2);
        tvPostText = findViewById(R.id.postTextView2);
        tvDate = findViewById(R.id.dateTextView2);
        ivPhoto = findViewById(R.id.postImageView2);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get post ID from extras
        String postId = getIntent().getExtras().getString(KEY_POST_ID);
        if (postId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_POST_ID);
        }

        //retrieve document to be displayed
        postRef = firestore.collection("posts").document(postId);
        postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
                assert post != null;
                tvPosterName.setText(post.getUser());
                tvPostText.setText(post.getText());
                tvDate.setText(post.getDate());

                GlideApp.with(getApplicationContext())
                        .load(post.getImageUri())
                        .override(800,400)
                        .into(ivPhoto);
            }
        });

    }
}