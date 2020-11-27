package com.github.ulkellyryan.comhra;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20);

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

//    public void loadNewsFeed(){
//        final List<Post> listPosts = new ArrayList<Post>(5);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("posts").orderBy("timestamp").limitToLast(5).get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        try{
//                            if(task.isSuccessful()){
//                                for(QueryDocumentSnapshot document : task.getResult() ){
//                                    Post post = document.toObject(Post.class);
//                                    listPosts.add(post);
//                                }
//                                TextView[] recentPosts = new TextView[5];
//                                recentPosts[0] = findViewById(R.id.post0);
//                                recentPosts[1] = findViewById(R.id.post1);
//                                recentPosts[2] = findViewById(R.id.post2);
//                                recentPosts[3] = findViewById(R.id.post3);
//                                recentPosts[4] = findViewById(R.id.post4);
//                                for(int i = 0, j = 4; (i < 5 && j >= 0); i++, j--){
//                                    recentPosts[i].setText(listPosts.get(j).toString());
//                                }
//
//                                ImageView photo = findViewById(R.id.profilePhoto);
//                                GlideApp.with(context)
//                                        .load(Uri.parse(listPosts.get(4).getImageUri()))
//                                        .override(600,600)
//                                        .into(photo);
//                            } else {
//                                Log.w("tag", "Error getting documents.", task.getException());
//                            }
//                        } catch (IndexOutOfBoundsException e) { //if there are fewer than 5 posts available stock posts will be uploaded and retrieved
//                            loadStockPosts();
//                            loadNewsFeed();
//                        }
//                    }
//                });
//    }

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