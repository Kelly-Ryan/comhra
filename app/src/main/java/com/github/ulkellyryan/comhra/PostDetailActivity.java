package com.github.ulkellyryan.comhra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostDetailActivity extends AppCompatActivity implements DeletePostDialogFragment.OnInputListener{

    public static final String KEY_POST_ID = "key_post_id";

    private TextView tvPosterName;
    private TextView tvPostText;
    private TextView tvDate;
    private ImageView ivPhoto;
    private String postId;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        DocumentReference postRef;
        Button deletePostButton;
        Button addCommentButton;
        tvPosterName = findViewById(R.id.posterNameTextView2);
        tvPostText = findViewById(R.id.postTextView2);
        tvDate = findViewById(R.id.dateTextView2);
        ivPhoto = findViewById(R.id.postImageView2);
        deletePostButton = findViewById(R.id.deletePostButton);
        addCommentButton = findViewById(R.id.addCommentButton);

        firestore = FirebaseFirestore.getInstance();

        // Get post ID from extras
        postId = getIntent().getExtras().getString(KEY_POST_ID);
        if (postId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_POST_ID);
        }

        deletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(KEY_POST_ID, postId);
                DeletePostDialogFragment dialog = new DeletePostDialogFragment();
                dialog.setArguments(bundle);    //pass postId to dialog
                dialog.show(getSupportFragmentManager(), "DeletePostDialog");
            }
        });

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });

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

    public void deletePost(String postId){
        firestore.collection("posts").document(postId).delete();
        Intent intent = new Intent(this, NewsFeedActivity.class);
        Toast.makeText(this, "Post deleted.", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
    
    public void addComment(){
        //TODO
    }
}