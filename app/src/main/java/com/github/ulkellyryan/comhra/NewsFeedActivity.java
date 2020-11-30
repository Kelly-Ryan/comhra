package com.github.ulkellyryan.comhra;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NewsFeedActivity extends AppCompatActivity {

    public static final String KEY_POST_ID = "key_post_id";
    private FirestoreRecyclerAdapter<Post, PostViewHolder> adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        recyclerView = (RecyclerView) findViewById(R.id.newsFeedRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Query query = FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new FirestoreRecyclerAdapter<Post, PostViewHolder>(options) {
            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.post, group, false);

                return new PostViewHolder(view);
            }

            @Override
            public void onBindViewHolder(PostViewHolder holder, int position, Post model){
                holder.setItem(model);
            }
        };
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        DocumentSnapshot post = adapter.getSnapshots().getSnapshot(position);
                        String postId = post.getReference().getId();

                        Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                        intent.putExtra(KEY_POST_ID, postId);

                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        //TODO if user is post owner display option to delete
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentSnapshot post = adapter.getSnapshots().getSnapshot(position);
                        String postId = post.getReference().getId();

                        db.collection("posts").document(postId).
                                delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //TODO display dialog to delete post
                                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                                        Toast.makeText(getApplicationContext(),"Post deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Error deleting post: ", e);
                                        Toast.makeText(getApplicationContext(),"Error deleting post: " + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
        );
    }

    //start NewPostActivity
    public void createNewPost(View view){
        Intent intent = new Intent(this, NewPostActivity.class);
        startActivity(intent);
    }

    public void viewProfile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void loadStockPosts(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Welcome post
        db.collection("posts").add(new Post("Welcome to Comhrá!", "admin", Timestamp.now()));
    }
}