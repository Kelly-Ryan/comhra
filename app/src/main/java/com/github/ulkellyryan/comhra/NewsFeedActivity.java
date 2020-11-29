package com.github.ulkellyryan.comhra;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NewsFeedActivity extends AppCompatActivity {

    private static Context context;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FirestoreRecyclerAdapter<Post, PostViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        context = getApplicationContext();

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
    }

    public static Context getContext(){
        return context;
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
        //preloaded posts
        Post post1 = new Post("Welcome to Comhrá!", "admin", Timestamp.now());
        db.collection("posts").add(post1);
        Post post2 = new Post("Register today!", "admin", Timestamp.now());
        db.collection("posts").add(post2);
        Post post3 = new Post("Login to view posts!", "admin", Timestamp.now());
        db.collection("posts").add(post3);
        Post post4 = new Post("Create your own posts!", "admin", Timestamp.now());
        db.collection("posts").add(post4);
        Post post5 = new Post("Tell us what you think!", "admin", Timestamp.now());
        db.collection("posts").add(post5);
    }
}