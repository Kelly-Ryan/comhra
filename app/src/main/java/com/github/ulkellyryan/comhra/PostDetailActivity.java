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

import java.util.Objects;

public class PostDetailActivity extends AppCompatActivity implements DeletePostDialogFragment.OnInputListener, DeleteCommentDialogFragment.OnInputListener{

    public static final String KEY_POST_ID = "key_post_id";
    public static final String KEY_COMMENT_ID = "key_comment_id";

    private TextView tvPosterName;
    private TextView tvPostText;
    private TextView tvDate;
    private ImageView ivPhoto, profilePhoto;
    private String postId, commentId;
    Post post;

    private FirebaseFirestore firestore;
    private FirestoreRecyclerAdapter<Comment, CommentViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        tvPosterName = findViewById(R.id.posterName);
        tvPostText = findViewById(R.id.postText);
        tvDate = findViewById(R.id.dateText);
        ivPhoto = findViewById(R.id.postImage);
        profilePhoto = findViewById(R.id.profilePhotoPost);
        Button deletePostButton = findViewById(R.id.deletePost);
        Button addCommentButton = findViewById(R.id.addComment);

        firestore = FirebaseFirestore.getInstance();

        // Get postId from extras
        postId = getIntent().getExtras().getString(KEY_POST_ID);

        //retrieve document to be displayed
        DocumentReference postRef = firestore.collection("posts").document(postId);
        postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                post = documentSnapshot.toObject(Post.class);
                assert post != null;

                tvPosterName.setText(post.getUser());
                tvPostText.setText(post.getText());
                tvDate.setText(post.getDate());

                GlideApp.with(getApplicationContext())
                        .load(post.getProfilePhoto())
                        .into(profilePhoto);

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
                .orderBy("timestamp", Query.Direction.ASCENDING);

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
            public void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model){
                holder.setItem(model);
            }
        };
        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }

                    //select comment for deletion
                    @Override
                    public void onLongItemClick(View view, int position) {
                        DocumentSnapshot documentSnapshot = adapter.getSnapshots().getSnapshot(position);
                        commentId = documentSnapshot.getReference().getId();
                        Comment comment = documentSnapshot.toObject(Comment.class);

                        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                        assert fbuser != null;

                        if (fbuser.getUid().equals(comment.getUid())) {
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_POST_ID, postId);
                            bundle.putString(KEY_COMMENT_ID, commentId);

                            DeleteCommentDialogFragment dialog = new DeleteCommentDialogFragment();
                            dialog.setArguments(bundle);    //pass commentId to dialog
                            dialog.show(getSupportFragmentManager(), "DeleteCommentDialog");

                        } else {
                            Toast.makeText(getApplicationContext(), "You do not have permission to delete this comment.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment(v);
            }
        });

        deletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
                assert fbuser != null;

                if(fbuser.getUid().equals(post.getUid())){
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_POST_ID, postId);
                    DeletePostDialogFragment dialog = new DeletePostDialogFragment();
                    dialog.setArguments(bundle);    //pass postId to dialog
                    dialog.show(getSupportFragmentManager(), "DeletePostDialog");
                } else {
                    Toast.makeText(getApplicationContext(), "You do not have permission to delete this post.", Toast.LENGTH_SHORT).show();
                }
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
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        EditText newComment = findViewById(R.id.newComment);

        assert fbuser != null;
        Comment comment = new Comment(fbuser.getDisplayName(), newComment.getText().toString(), Objects.requireNonNull(fbuser.getPhotoUrl()).toString(), fbuser.getUid(), Timestamp.now());
        firestore.collection("posts").document(postId)
                 .collection("comments").add(comment);

        Toast.makeText(this, "Comment submitted.", Toast.LENGTH_SHORT).show();

        //refresh PostDetailActivity where new comment can be viewed
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra(KEY_POST_ID, postId);
        startActivity(intent);
    }

    public void deleteComment(String postId, String commentId){
        firestore.collection("posts").document(postId)
                .collection("comments").document(commentId).delete();

        Toast.makeText(this, "Comment deleted.", Toast.LENGTH_SHORT).show();
    }
}