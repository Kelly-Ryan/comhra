package com.github.ulkellyryan.comhra;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ViewProfileFragment extends Fragment {

    private TextView  name, email, pronouns, bio, location;
    private ImageView profilePhoto;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_profile, container, false);

        name = v.findViewById(R.id.profileName3);
        pronouns = v.findViewById(R.id.profilePronouns3);
        bio = v.findViewById(R.id.profileBioText);
        location = v.findViewById(R.id.profileLocationText);
        email = v.findViewById(R.id.profileEmailText);
        profilePhoto = v.findViewById(R.id.profilePhotoView);

        return v;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        assert fbuser != null;

        DocumentReference docRef = firestore.collection("users").document(fbuser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    User user = document.toObject(User.class);
                    if (document.exists()) {
                        assert user != null;
                        name.setText(user.getName());
                        pronouns.setText(user.getPronouns());
                        bio.setText(user.getBio());
                        location.setText(user.getLocation());
                        email.setText(user.getEmail());

                        GlideApp.with(requireContext())
                                .load(user.getPhotoUri())
                                .into(profilePhoto);

                        Log.d("TAG", "DocumentSnapshot data retrieved: " + document.getData());
                    } else {
                        firestore.collection("users").document(fbuser.getUid()).set(new User(fbuser.getDisplayName(), fbuser.getEmail(), Objects.requireNonNull(fbuser.getPhotoUrl()).toString(), fbuser.getUid()));
                        Log.d("TAG", "User added to Users collection.");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        Button editProfileButton = view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_viewProfileFragment_to_editProfileFragment);
            }
        });

        Button backButton = view.findViewById(R.id.backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getContext(), NewsFeedActivity.class);
                startActivity(intent);
            }
        });
    }
}