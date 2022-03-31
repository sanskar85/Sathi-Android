package com.abbvmk.sathi.screens.Login;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.Views.ProgressButton.ProgressButton;
import com.abbvmk.sathi.screens.EditProfile.EditProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class Login extends AppCompatActivity implements ProgressButton.OnClickListener {


    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    ProgressButton submit;
    EditText phone;
    ConstraintLayout phoneCard;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_login);


        submit = findViewById(R.id.submit_btn);
        phoneCard = findViewById(R.id.input_wrapper);
        phone = findViewById(R.id.etPhone);
        submit.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(Login.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(Login.this, "OTP quota for Sathi is reached. Please try again later.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, "Cannot send OTP. Please try again later.", Toast.LENGTH_SHORT).show();
                }

            }

        };
    }

    @Override
    public void onClick() {
        if (!submit.isViewEnabled()) return;
        if (phone.getText().length() != 10) {
            Toast.makeText(this, "Phone number must be of 10 digit", Toast.LENGTH_SHORT).show();
            return;
        }
        phone.setEnabled(false);
        submit.setViewEnabled(false);
        submit.buttonActivated();
        String phoneNumber = "+91" + phone.getText();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(Login.this, "Unable to verify phone.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            phoneCard.setEnabled(false);
            submit.setCardBackgroundColor(ContextCompat.getColor(this, R.color.success));
            submit.buttonFinished("Verified");
            Firebase.fetchMyProfile(success -> {
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(this, EditProfile.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }, 500);
            });

        }
    }


    private boolean isBackPressed = false;

    @Override
    public void onBackPressed() {
        if (isBackPressed) {
            super.onBackPressed();
        } else {
            isBackPressed = true;
            new Handler().postDelayed(() -> {
                Toast.makeText(this, "Press back again to exit Sathi", Toast.LENGTH_SHORT).show();
            }, 1000);
        }
    }
}
