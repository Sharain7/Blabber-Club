package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView ;
    ImageView searchButton;
    ChatFragment chatFragment ;
    ProfileFragment profileFragment;
    UserModel userModel  ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment() ;
        bottomNavigationView = findViewById(R.id.bottomNavView);
        searchButton  = findViewById(R.id.btn_search);
        userModel = new UserModel();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout , chatFragment).commit();

                }
                if(item.getItemId() == R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout , profileFragment).commit();

                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , SearchActivity.class);
                startActivity(intent);

            }
        });
        getFCMToken();

    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String token  = task.getResult();
                    FirebaseUtil.currentUserDetails().update("fcmToken" , token );


                }
            }
        });
    }

}