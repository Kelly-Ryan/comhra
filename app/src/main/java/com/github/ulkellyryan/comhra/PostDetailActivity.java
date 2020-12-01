package com.github.ulkellyryan.comhra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostDetailActivity extends AppCompatActivity implements DeletePostDialogFragment.OnInputListener{

    public static final String KEY_POST_ID = "key_post_id";

    private TextView tvPosterName;
    private TextView tvPostText;
    private TextView tvDate;
    private ImageView ivPhoto;
    private String postId;

    private FirebaseFirestore firestore;
    private FirestoreRecyclerAdapter<Comment, CommentViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        tvPosterName = findViewById(R.id.posterNameTextView2);
        tvPostText = findViewById(R.id.postTextView2);
        tvDate = findViewById(R.id.dateTextView2);
        ivPhoto = findViewById(R.id.postImageView2);
        Button deletePostButton = findViewById(R.id.deletePostButton);
        Button addCommentButton = findViewById(R.id.addCommentButton);

        firestore = FirebaseFirestore.getInstance();

        // Get postId from extras
        postId = getIntent().getExtras().getString(KEY_POST_ID);

        //retrieve document to be displayed
        DocumentReference postRef = firestore.collection("posts").document(postId);
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

        //retrieve and display comments
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.commentsRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Query query = FirebaseFirestore.getInstance()
                .collection("posts").document(postId).collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirestoreRecyclerAdapter<Comment, CommentViewHolder>(options) {
            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.comment, group, false);

                return new CommentViewHolder(view);
            }

            @Override
            public void onBindViewHolder(CommentViewHolder holder, int position, Comment model){
                holder.setItem(model);
            }
        };
        recyclerView.setAdapter(adapter);

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment(v);
            }
        });

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
    }

    public void deletePost(String postId){
        firestore.collection("posts").document(postId).delete();
        Intent intent = new Intent(this, NewsFeedActivity.class);
        Toast.makeText(this, "Post deleted.", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
    
    public void addComment(View view){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        EditText newComment = findViewById(R.id.newCommentText);

        Comment comment = new Comment(user.getDisplayName(), newComment.getText().toString(), Timestamp.now());
        firestore.collection("posts").document(postId)
                 .collection("comments").add(comment);

        //refresh PostDetailActivity where new comment can be viewed
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra(KEY_POST_ID, postId);
        startActivity(intent);
    }
}