package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameActivity extends AppCompatActivity {
    EditText inputUserName;
    ProgressBar progressBar;
    Button btnLetMeIn;
    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);
        initUI();
    }

    private void initUI() {
        phoneNumber = getIntent().getExtras().getString("phone");

        inputUserName = findViewById(R.id.edtxUsername);
        progressBar = findViewById(R.id.usernamePb);
        btnLetMeIn = findViewById(R.id.btn_letmeIn);
        getUserNameFromFirebase();

        btnLetMeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUserName();
            }
        });

    }

    private void setUserName() {

        String username = inputUserName.getText().toString();
        if (username.isEmpty() || username.length() < 3) {
            inputUserName.setError("Username should be greater than 3 characters");
            return;
        }
        setInProgress(true);
        if (userModel != null) {
            userModel.setUsername(username);
        } else {
            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserId());
        }
        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    //otp verified already
                    //user created
                    Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

    }


    private void setInProgress(boolean inProgress) {
        //this method will trigger the progress bar loading state
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            btnLetMeIn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLetMeIn.setVisibility(View.VISIBLE);
        }

    }

    private void getUserNameFromFirebase() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        inputUserName.setText(userModel.getUsername());
                    }
                }

            }
        });


    }
}