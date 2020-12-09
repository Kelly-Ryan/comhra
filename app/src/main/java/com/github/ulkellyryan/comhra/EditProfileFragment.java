package com.github.ulkellyryan.comhra;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class EditProfileFragment extends Fragment {

    private EditText name, email, pronouns, bio, location;
    private ImageView profilePhoto;
    FirebaseUser fbuser;

    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 103;
    StorageReference storageReference;
    AppCompatImageButton camButton;
    String currentPhotoPath;
    String imageUri;

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
        camButton = v.findViewById(R.id.profilePhotoButton);

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
                    assert user != null;
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

        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
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

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getContext(), "Camera permission is required to use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(), "com.github.ulkellyryan.comhra.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    /**I have tried doing this several different ways but onActivityResult() does not get called. It works when it is inside an Activity like NewPostActivity but
    I cannot get it to work inside this fragment.**/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                profilePhoto.setImageURI(Uri.fromFile(f));         //display image in ImageView
                Uri contentUri = Uri.fromFile(f);
                uploadImageToFirebase(f.getName(), contentUri);
                Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(f));
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss"). format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                profilePhoto.setImageURI(contentUri);
                uploadImageToFirebase(imageFileName, contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri: " + imageFileName);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        //save file in app folder
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {
        final StorageReference image = storageReference.child("pictures/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUri = uri.toString();      //url of image stored in Firebase Storage
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Upload Failed.", Toast.LENGTH_SHORT).show();
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