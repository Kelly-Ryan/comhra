package com.github.ulkellyryan.comhra;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class PostViewHolder extends RecyclerView.ViewHolder{

    private final TextView tvPosterName;
    private final TextView tvPostText;
    private final TextView tvDate;
    private final ImageView ivPhoto;
    private final ImageView ivProfilePhoto;

    public PostViewHolder(View itemView){
        super(itemView);
        tvPosterName = itemView.findViewById(R.id.posterNameTextView);
        tvPostText = itemView.findViewById(R.id.postTextView);
        tvDate = itemView.findViewById(R.id.dateTextView);
        ivPhoto = itemView.findViewById(R.id.postImageView);
        ivProfilePhoto = itemView.findViewById(R.id.profilePhoto);
    }

    @SuppressLint("RestrictedApi")
    public void setItem(Post item){
        tvPosterName.setText(item.getUser());
        tvPostText.setText(item.getText());
        tvDate.setText(item.getDate());

        GlideApp.with(getApplicationContext())
                .load(item.getProfilePhoto())
                .into(ivProfilePhoto);

        GlideApp.with(getApplicationContext())
                .load(item.getImageUri())
                .override(800,400)
                .into(ivPhoto);
    }

}
