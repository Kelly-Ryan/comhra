package com.github.ulkellyryan.comhra;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeleteCommentDialogFragment extends DialogFragment {

    private static final String TAG = "PostDetailActivity";
    private static final String KEY_POST_ID = "key_post_id";
    private static final String KEY_COMMENT_ID = "key_comment_id";
    private String postId, commentId;

    public DeleteCommentDialogFragment.OnInputListener onInputListener;

    public interface OnInputListener {
        void deleteComment(String postId, String commentId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_comment_dialog, container, false);

        Bundle bundle = this.getArguments();
        postId = bundle.getString(KEY_POST_ID);
        commentId = bundle.getString(KEY_COMMENT_ID);

        Button confirmDeleteCommentButton = view.findViewById(R.id.confirmDeleteComment);
        Button cancelDeleteCommentButton = view.findViewById(R.id.cancelDeleteComment);

        cancelDeleteCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        confirmDeleteCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInputListener.deleteComment(postId, commentId);
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onInputListener = (DeleteCommentDialogFragment.OnInputListener) getActivity();
            Log.d(TAG, "onAttach: " + onInputListener);
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: " + e.getMessage());
        }
    }
}
