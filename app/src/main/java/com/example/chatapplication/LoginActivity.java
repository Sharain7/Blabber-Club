package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneNumberInput;
    ProgressBar progressBar;
    Button sendOtp ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
    }

    private void initUI() {

        countryCodePicker = findViewById(R.id.ccpPicker);
        phoneNumberInput = findViewById(R.id.edtxLoginMobileNumber);
        progressBar = findViewById(R.id.loginPb);
        progressBar.setVisibility(View.GONE);
        sendOtp  = findViewById(R.id.btn_sendOtp);
        countryCodePicker.registerCarrierNumberEditText(phoneNumberInput);
        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSendOtp();
            }
        });
    }

    private void handleSendOtp() {
        //1. check if the phone is valid
        if(!countryCodePicker.isValidFullNumber()){
            phoneNumberInput.setError("Phone number is not valid");
            return ;
        }
        Intent intent  = new Intent(LoginActivity.this  , LoginOtpActivity.class);
        intent.putExtra("phone" , countryCodePicker.getFullNumberWithPlus());
        startActivity(intent);


    }
}