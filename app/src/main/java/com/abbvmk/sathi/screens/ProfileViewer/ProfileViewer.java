package com.abbvmk.sathi.screens.ProfileViewer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.abbvmk.sathi.Fragments.Profile.Profile;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;

public class ProfileViewer extends AppCompatActivity {
    private User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_profile_viewer);
        mUser = (User) getIntent().getSerializableExtra("user");
        String userID = getIntent().getStringExtra("userID");

        if (mUser == null && userID == null) finish();

        Profile profile;
        if (mUser != null)
            profile = new Profile(getApplicationContext(), mUser);
        else
            profile = new Profile(getApplicationContext(), userID);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profileFragmentContainer, profile)
                .commit();
    }
}