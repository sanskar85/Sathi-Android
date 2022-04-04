package com.abbvmk.sathi.screens.EditProfile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.ChildDetail;
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.User.UserValidationException;
import com.abbvmk.sathi.Views.ChildDetailView.ChildDetailView;
import com.abbvmk.sathi.Views.ProgressButton.ProgressButton;
import com.abbvmk.sathi.screens.LandingPage.LandingPage;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ChildDetails extends AppCompatActivity implements ProgressButton.OnClickListener {

    private User user;
    private boolean fromHome;
    private TextInputEditText childCount;
    private LinearLayoutCompat childrenLayout;
    private ProgressButton submit;
    private ArrayList<ChildDetailView> childView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_child_details);
        Intent intent = getIntent();
        fromHome = intent.getBooleanExtra("fromHome", false);
        user = AuthHelper.getLoggedUser();
        initView();
    }

    private void initView() {
        childCount = findViewById(R.id.childCountET);
        childrenLayout = findViewById(R.id.childrenLayout);
        submit = findViewById(R.id.submit_btn);
        submit.setText("Save");
        submit.setOnClickListener(this);
        childCount.setText(String.valueOf(user.getChildCount()));
        generateViews(user.getChildCount());
        childCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = editable.toString();
                if (!TextUtils.isEmpty(value) && TextUtils.isDigitsOnly(value)) {
                    int count = Integer.parseInt(editable.toString());
                    generateViews(count);
                } else {
                    generateViews(0);
                }
            }
        });

    }

    private void generateViews(int count) {
        user.setChildCount(count);
        childrenLayout.removeAllViews();
        childView = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ChildDetail detail = user.getChildDetails() != null &&
                    user.getChildDetails().size() >= i
                    ? user.getChildDetails().get(i - 1)
                    : new ChildDetail();
            ChildDetailView view = new ChildDetailView(this, i, detail);
            childrenLayout.addView(view);
            childView.add(view);
        }
    }

    @Override
    public void onClick() {
        if (!submit.isViewEnabled()) {
            return;
        }
        ArrayList<ChildDetail> childDetails = new ArrayList<>();
        try {
            for (ChildDetailView view : childView) {
                view.resolveData();
                view.getChildDetail().validate();
                childDetails.add(view.getChildDetail());
            }
        } catch (UserValidationException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception e) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {


            submit.setViewEnabled(false);
            submit.buttonActivated();
            user.setChildDetails(childDetails);

            Firebase
                    .saveProfile(user, success -> {
                        if (success) {
                            MainApplication.fetchUsers();
                            submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.success));
                            submit.buttonFinished("Saved");
                            new Handler().postDelayed(() -> {
                                if (!fromHome) {
                                    startActivity(new Intent(getApplicationContext(), LandingPage.class));
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }
                                finish();
                            }, 2000);
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to save your profile", Toast.LENGTH_SHORT).show();

                            submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.failure));
                            submit.buttonFinished("Failure");
                            new Handler().postDelayed(() -> {
                                submit.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.progress_bar_background));
                                submit.buttonFinished("Save");
                                submit.setViewEnabled(true);

                            }, 3000);
                        }
                    });
        }).start();

    }
}