package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if( FirebaseUtil.isLoggedIn() && getIntent().getExtras()!=null){
            //from notification
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                //move to the chat activity
                                UserModel userModel = task.getResult().toObject(UserModel.class);
                                Intent mainIntent = new Intent(SplashActivity.this , MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(mainIntent);
                                Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
                                AndroidUtil.passModel(intent, userModel );
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }else{
            //not from notification
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FirebaseUtil.isLoggedIn()){
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                    else{
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                }
            },1000);
        }


    }
}