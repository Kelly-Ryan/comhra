package com.github.ulkellyryan.comhra;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewProfileFragment extends Fragment {

    private TextView  name, email;
    private ImageView profilePhoto;
    private final Uri defaultProfilePhoto = Uri.parse("https://firebasestorage.googleapis.com/v0/b/comhra-35d60.appspot.com/o/pictures%2Fdefault-avatar.png?alt=media&token=912372e4-9dca-4f78-be13-d28dda1577ba");
    private FirebaseUser fbuser;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_profile, container, false);

        name = v.findViewById(R.id.profileNameView);
        email = v.findViewById(R.id.profileEmailText);
        profilePhoto = v.findViewById(R.id.profilePhotoView);

        return v;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        fbuser = FirebaseAuth.getInstance().getCurrentUser();
        assert fbuser != null;
        String nametext = fbuser.getDisplayName();
        String emailtext = fbuser.getEmail();

        name.setText(nametext);
        email.setText(emailtext);

        if(fbuser.getPhotoUrl() == null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(defaultProfilePhoto)
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
        }

        GlideApp.with(requireContext())
                .load(fbuser.getPhotoUrl())
                .into(profilePhoto);

        Button editProfileButton = view.findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_viewProfileFragment_to_editProfileFragment);
            }
        });
    }

}