package com.github.ulkellyryan.comhra;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DeletePostDialogFragment extends DialogFragment {

    private static final String TAG = "PostDetailActivity";
    private static final String KEY_POST_ID = "key_post_id";
    private String postId;

    public OnInputListener onInputListener;

    public interface OnInputListener {
        void deletePost(String postId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_post_dialog, container, false);

        Bundle bundle = this.getArguments();
        postId = bundle.getString(KEY_POST_ID);

        Button confirmDeletePostButton = view.findViewById(R.id.confirmDeletePostButton);
        Button cancelDeletePostButton = view.findViewById(R.id.cancelDeletePostButton);

        cancelDeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        confirmDeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInputListener.deletePost(postId);
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onInputListener = (OnInputListener) getActivity();
            Log.d(TAG, "onAttach: " + onInputListener);
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: " + e.getMessage());
        }
    }
}