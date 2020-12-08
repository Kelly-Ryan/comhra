package com.github.ulkellyryan.comhra;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class EditProfileFragment extends Fragment {

    private EditText name, email, pronouns, bio, location;
    private DatePicker birthday;
    private ImageView profilePhoto;
    FirebaseUser fbuser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        name = v.findViewById(R.id.profileEditDisplayName);
        pronouns = v.findViewById(R.id.profileEditPronouns);
        bio = v.findViewById(R.id.profileBioEditText);
        location = v.findViewById(R.id.profileLocationEditText);
        email = v.findViewById(R.id.profileEmailEditText);
        profilePhoto = v.findViewById(R.id.profilePhotoView2);

        return v;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        fbuser = FirebaseAuth.getInstance().getCurrentUser();
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
                        name.setText(user.getName());
                        pronouns.setText(user.getPronouns());
                        bio.setText(user.getBio());
                        location.setText(user.getLocation());
                        email.setText(user.getEmail());

                        GlideApp.with(requireContext())
                                .load(fbuser.getPhotoUrl())
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

        Button saveChangesButton = view.findViewById(R.id.saveButton);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFirebaseProfileChanges();
                saveUserInfo();
            }
        });

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_editProfileFragment_to_viewProfileFragment);
            }
        });
    }

    public void saveFirebaseProfileChanges(){
        //update display name
        UserProfileChangeRequest nameUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString())
                .build();

        fbuser.updateProfile(nameUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User name updated.");
                        }
                    }
                });

        //update email address
        fbuser.updateEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User email address updated.");
                        }
                    }
                });

        //update profile photo
        UserProfileChangeRequest profilePhotoUpdate = new UserProfileChangeRequest.Builder()
                //.setPhotoUri(profilePhotoUri)
                .build();

        fbuser.updateProfile(profilePhotoUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User profile photo updated.");
                        }
                    }
                });

        Toast.makeText(getContext(), "Profile changes saved successfully.", Toast.LENGTH_SHORT).show();

        fbuser.reload();
    }

    public void saveUserInfo(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        assert fbuser != null;
        firestore.collection("users").document(fbuser.getUid())
                .update("name", fbuser.getDisplayName(),
                        "email", fbuser.getEmail(),
                        "bio", bio.getText().toString(),
                        "location", location.getText().toString(),
                        "pronouns", pronouns.getText().toString());

    }
}