package com.github.ulkellyryan.comhra;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CommentViewHolder extends RecyclerView.ViewHolder{

    private final TextView tvCommenterName;
    private final TextView tvCommentText;
    private final TextView tvDate;
    private final ImageView profilePhoto;

    public CommentViewHolder(View itemView){
        super(itemView);
        tvCommenterName = itemView.findViewById(R.id.commenterName);
        tvCommentText = itemView.findViewById(R.id.commentText);
        tvDate = itemView.findViewById(R.id.commentDateTime);
        profilePhoto = itemView.findViewById(R.id.commentProfilePhoto);
    }

    @SuppressLint("RestrictedApi")
    public void setItem(Comment item){
        tvCommenterName.setText(item.getUser());
        tvCommentText.setText(item.getText());
        tvDate.setText(item.getDate());

        //profile photo
        GlideApp.with(getApplicationContext())
                .load(item.getProfilePhoto())
                .into(profilePhoto);
    }

}
