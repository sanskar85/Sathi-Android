package com.abbvmk.sathi.screens.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.screens.EditProfile.EditProfile;
import com.abbvmk.sathi.screens.LandingPage.LandingPage;
import com.abbvmk.sathi.screens.Login.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class Splash extends AppCompatActivity {
    static final int SPLASH_SCREEN_TIME = 1500;
    private String type;
    private String id;
    private boolean notice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash);

        Intent intent = getIntent();
        notice = intent.getBooleanExtra("notice_notification", false);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    // Get deep link from result (may be null if no link is found)
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String url = deepLink.toString();
                            url = url.substring(url.indexOf("?") + 1);
                            type = url.split("=")[0];
                            id = url.split("=")[1];

                        }

                    }
                });

        AuthHelper.fetchUserProfile();
        MainApplication.fetchUsers();


        new Handler().postDelayed(() -> {
            final boolean loggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;
            final User user = AuthHelper.getLoggedUser();
            Intent act;
            if (loggedIn) {
                if (user == null || TextUtils.isEmpty(user.getName())) {
                    act = new Intent(this, EditProfile.class);
                } else {
                    act = new Intent(this, LandingPage.class);
                    act.putExtra(type, id);
                    act.putExtra("notice", notice);
                }
                startActivity(act);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                act = new Intent(this, Login.class);
                startActivity(act);
                overridePendingTransition(0, 0);
            }
            finish();
        }, SPLASH_SCREEN_TIME);
    }
}