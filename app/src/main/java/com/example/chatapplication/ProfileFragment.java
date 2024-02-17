package com.example.chatapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class ProfileFragment extends Fragment {

    ImageView profileImage ;
    EditText usernameInput ;
    EditText phoneNumberInput ;
    ProgressBar progressBar ;
    TextView btnLogout ;
    UserModel userModel ;
    Button buttonUpdateProfile ;
    ActivityResultLauncher<Intent>imagePickLauncher;
    Uri selectedImageUri  ;



    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult() ,
                result -> {
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                if(data!=null && data.getData()!=null){
                    //image selected by user uri
                    selectedImageUri = data.getData() ;
                    AndroidUtil.setProfilePicture(getContext() , selectedImageUri , profileImage);

                }
            }

                }
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage  = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_edit_text_username);
        phoneNumberInput = view.findViewById(R.id.profile_edit_text_phone);
        progressBar = view.findViewById(R.id.profilePb);
        btnLogout = view.findViewById(R.id.btnLogout);
        buttonUpdateProfile  = view.findViewById(R.id.btn_updateProfile);
        getUserData();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogoutClick();
            }
        });
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleUpdateProfile();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProfileFragment.this).cropSquare().compress(512).maxResultSize(512,512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickLauncher.launch(intent);
                                return null;
                            }
                        });
            }
        });

        return view;

    }

    private void handleUpdateProfile() {
        String updatedName = usernameInput.getText().toString().trim();
        if (updatedName.isEmpty() || updatedName.length() < 3) {
            usernameInput.setError("Username should be greater than 3 characters");
            return;
        }

        userModel.setUsername(updatedName);
        setInProgress(true);
        if(selectedImageUri!=null){
            FirebaseUtil.getCurrentProfileStorageReference().putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        updateFireBaseWithUpdatedUsername();
                }
            });
        }
        else{
            //this case where the usr just wants to update the details excpet the profile
            updateFireBaseWithUpdatedUsername();
        }






    }

    private void updateFireBaseWithUpdatedUsername() {
        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    AndroidUtil.showToast(getContext() , "Updated successfully");
                }
                else{
                    AndroidUtil.showToast(getContext() , "Update failed");

                }
            }
        });
    }

    private void handleLogoutClick() {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FirebaseUtil.logout();
                            Intent intent = new Intent(getContext()  , SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                }
        );

    }

    private void setInProgress(boolean inProgress) {
        //this method will trigger the progress bar loading state
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            buttonUpdateProfile.setVisibility(View.GONE);

        } else {
            progressBar.setVisibility(View.GONE);
            buttonUpdateProfile.setVisibility(View.VISIBLE);


        }
    }


        private void getUserData() {
            setInProgress(true);

            FirebaseUtil.getCurrentProfileStorageReference().getDownloadUrl()
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        Uri uri = task.getResult();
                                        AndroidUtil.setProfilePicture(getContext() , uri , profileImage);
                                    }
                                }
                            });
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);
                    if(userModel!=null){
                        usernameInput.setText(userModel.getUsername());
                        phoneNumberInput.setText(userModel.getPhoneNumber());
                    }



                }
            }
        });
    }
}