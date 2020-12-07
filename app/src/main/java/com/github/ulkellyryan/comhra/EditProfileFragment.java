package com.github.ulkellyryan.comhra;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class EditProfileFragment extends Fragment {

    private EditText name, email;
    private ImageView profilePhoto;
    FirebaseUser fbuser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        name = v.findViewById(R.id.profileEditDisplayName);
        email = v.findViewById(R.id.profileEmailEditText);
        profilePhoto = v.findViewById(R.id.profilePhotoEdit);

        return v;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        fbuser = FirebaseAuth.getInstance().getCurrentUser();
        assert fbuser != null;

        name.setText(fbuser.getDisplayName());
        email.setText(fbuser.getEmail());

        GlideApp.with(requireContext())
                .load(fbuser.getPhotoUrl())
                .into(profilePhoto);

        Button saveChangesButton = view.findViewById(R.id.saveButton);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
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

    public void saveProfileChanges(){

        //update display name
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString())
                //.setPhotoUri(profilePhotoUri)
                .build();

        fbuser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User profile updated.");
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

        Toast.makeText(getContext(), "Profile changes saved successfully.", Toast.LENGTH_SHORT).show();

        fbuser.reload();
    }
}