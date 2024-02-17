package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOtpActivity extends AppCompatActivity {

    String phoneNumber;
    EditText otpInput;
    ProgressBar progressBar;
    Button btnNext;
    TextView tvResendOtp;
    FirebaseAuth mAuth;
    String verificationCode; //to verify the otp
    PhoneAuthProvider.ForceResendingToken resendingToken;
    Long timerSeconds = 60L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);
        initUI();
        //test data to check if the data goes into the firebase
//        Map<String,String> data = new HashMap<>();
//        FirebaseFirestore.getInstance().collection("test").add(data);
    }

    private void initUI() {
        phoneNumber = getIntent().getExtras().getString("phone");
        btnNext = findViewById(R.id.btn_next);
        otpInput = findViewById(R.id.edtxOtp);
        progressBar = findViewById(R.id.otpPb);
        tvResendOtp = findViewById(R.id.tv_resendCode);
        mAuth = FirebaseAuth.getInstance();
        sendOtp(phoneNumber, false);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredOtp = otpInput.getText().toString().trim();
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp); //provide us the credentials
                signIn(phoneAuthCredential);
                setInProgress(true);
            }
        });
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOtp(phoneNumber , true);
            }
        });
    }

    private void sendOtp(String phoneNumber, boolean isResend) {
        //here the otp will be send and also will be triggering resend code
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timerSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        //otp has been entered and verified automatically and  no need enter the otp
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        AndroidUtil.showToast(getApplicationContext(), "OTP Verification Failed");
                        setInProgress(false);

                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        AndroidUtil.showToast(getApplicationContext(), "OTP Sent Successfully");
                        setInProgress(false);

                    }
                });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void startResendTimer() {
        //this will handle resend
        tvResendOtp.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerSeconds--;
                tvResendOtp.setText("Resend OTP in " + timerSeconds + " seconds");
                if (timerSeconds <= 0) {
                    timerSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResendOtp.setEnabled(true);
                        }
                    });

                }

            }
        }, 0, 1000);
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        //login part and go to the next activity
        //we will check the otp now
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    //the code is verified
                    Intent intent = new Intent(LoginOtpActivity.this, LoginUsernameActivity.class);
                    intent.putExtra("phone", phoneNumber);
                    startActivity(intent);

                } else {
                    AndroidUtil.showToast(getApplicationContext(), "OTP Verification Failed");
                }
            }
        });

    }

    private void setInProgress(boolean inProgress) {
        //this method will trigger the progress bar loading state
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
        }

    }
}