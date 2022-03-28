package com.abbvmk.sathi.screens.Login;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.abbvmk.sathi.Helper.API;
import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.Views.OTP_InputBox.OTP_InputBox;
import com.abbvmk.sathi.Views.ProgressButton.ProgressButton;
import com.abbvmk.sathi.screens.EditProfile.EditProfile;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity implements ProgressButton.OnClickListener {


    enum Status {
        OTP_NOT_SENT,
        OTP_SENT,
        VERIFIED
    }

    ;

    ProgressButton submit;
    Status curr_status = Status.OTP_NOT_SENT;
    EditText phone;
    ConstraintLayout phoneCard;
    OTP_InputBox otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_login);
        submit = findViewById(R.id.submit_btn);
        phoneCard = findViewById(R.id.input_wrapper);
        phone = findViewById(R.id.etPhone);
        otp = findViewById(R.id.otp_input_box);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick() {
        if (!submit.isViewEnabled()) return;
        if (curr_status == Status.OTP_NOT_SENT) {
            if (phone.getText().length() != 10) {
                Toast.makeText(this, "Phone number must be of 10 digit", Toast.LENGTH_SHORT).show();
                return;
            }

            submit.setViewEnabled(false);
            submit.buttonActivated();
            new Thread(() -> {

                Map<String, String> fields = new HashMap<>();
                fields.put("phone", String.valueOf(phone.getText()));
                Call<String> call = API.instance().loginRequest(fields);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        submit.setViewEnabled(true);
                        if (response.code() == 200) {
                            updateStatus(Status.OTP_SENT);
                        } else {

                            Toast.makeText(getApplicationContext(), "Unable to send OTP. Please try again later.", Toast.LENGTH_SHORT).show();
                            submit.buttonFinished("Continue");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        submit.setViewEnabled(true);
                        Toast.makeText(getApplicationContext(), "Unable to send OTP. Please try again later.", Toast.LENGTH_SHORT).show();
                        submit.buttonFinished("Continue");
                    }
                });
            }).start();
        } else if (curr_status == Status.OTP_SENT) {
            String otpText = otp.getOTP();
            if (otpText.length() != otp.size()) {
                Toast.makeText(this, "OTP must be of " + otp.size() + " digit", Toast.LENGTH_SHORT).show();
                return;
            }
            submit.setViewEnabled(false);
            submit.buttonActivated();
            new Thread(() -> {
                Map<String, String> fields = new HashMap<>();
                fields.put("phone", String.valueOf(phone.getText()));
                fields.put("otp", otpText);
                fields.put("notification_token", AuthHelper.getNotificationToken());
                Call<LoginResponse> call = API.instance().loginVerify(fields);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                        submit.setViewEnabled(true);
                        if (response.code() == 200 && response.body() != null) {
                            AuthHelper.saveAuthenticationToken(response.body().getAuthToken());
                            AuthHelper.saveUser(response.body().getUser());
                            updateStatus(Status.VERIFIED);
                        } else {
                            Toast.makeText(getApplicationContext(), "OTP verification Failed", Toast.LENGTH_SHORT).show();
                            submit.buttonFinished("Continue");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                        submit.setViewEnabled(true);
                        Toast.makeText(getApplicationContext(), "OTP verification Failed", Toast.LENGTH_SHORT).show();
                        System.out.println(t.getMessage());
                        submit.buttonFinished("Continue");
                    }
                });
            }).start();

        }
    }

    private void updateStatus(Status status) {
        curr_status = status;
        if (status == Status.OTP_NOT_SENT) {
            phoneCard.setVisibility(View.VISIBLE);
            otp.setVisibility(View.GONE);
            submit.buttonFinished("Continue");
            submit.setCardBackgroundColor(ContextCompat.getColor(this, R.color.progress_bar_background));
        } else if (status == Status.OTP_SENT) {
            phoneCard.setVisibility(View.GONE);
            otp.setVisibility(View.VISIBLE);
            submit.setCardBackgroundColor(ContextCompat.getColor(this, R.color.success));
            submit.buttonFinished("OTP Sent");
            new Handler().postDelayed(() -> {
                submit.setCardBackgroundColor(ContextCompat.getColor(this, R.color.progress_bar_background));
                submit.buttonFinished("Verify");
            }, 5000);
        } else if (status == Status.VERIFIED) {
            phoneCard.setVisibility(View.GONE);
            otp.setVisibility(View.VISIBLE);
            submit.setCardBackgroundColor(ContextCompat.getColor(this, R.color.success));
            submit.buttonFinished("Verified");
            new Handler().postDelayed(() -> {
                startActivity(new Intent(this, EditProfile.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 1000);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (curr_status == Status.OTP_SENT) {
            updateStatus(Status.OTP_NOT_SENT);
        } else {
            super.onBackPressed();
        }
    }
}
