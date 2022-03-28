package com.abbvmk.sathi.screens.LandingPage;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.abbvmk.sathi.BuildConfig;
import com.abbvmk.sathi.Fragments.Admin.Admin;
import com.abbvmk.sathi.Fragments.Home.Home;
import com.abbvmk.sathi.Fragments.Members.Members;
import com.abbvmk.sathi.Fragments.Profile.Profile;
import com.abbvmk.sathi.Helper.API;
import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.Helper.FilesHelper;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.screens.PostViewer.PostViewer;
import com.abbvmk.sathi.screens.ProfileViewer.ProfileViewer;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandingPage extends AppCompatActivity implements ChipNavigationBar.OnItemSelectedListener {

    ChipNavigationBar navigation;
    FrameLayout adminMenu;
    private boolean noticeSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_landing_page);

        Intent intent = getIntent();
        noticeSelected = intent.getBooleanExtra("notice", false);

        initViews();

        if (intent.hasExtra("user")) {
            Intent startIntent = new Intent(this, ProfileViewer.class);
            startIntent.putExtra("userID", intent.getStringExtra("user"));
            startActivity(startIntent);

        } else if (intent.hasExtra("post")) {

            Intent startIntent = new Intent(this, PostViewer.class);
            startIntent.putExtra("postID", intent.getStringExtra("post"));
            startActivity(startIntent);
        }

        fetchUpdates();
    }

    private void fetchUpdates() {
        new Thread(() -> {
            FilesHelper.downloadUpdate(this, -1, (file) -> {
                Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("New Update found");
                alertDialog.setMessage("Do you want to install latest update?");
                alertDialog.setIcon(R.drawable.logo);
                alertDialog.setCancelable(false);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sure", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    startActivity(intent);
                });

                alertDialog.show();


            });

        }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        adminMenu = findViewById(R.id.admin);
        adminMenu.setVisibility(View.GONE);
        if (AuthHelper.isAdmin()) {
            adminMenu.setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        navigation = findViewById(R.id.bottom_navigation);
        navigation.setMenuResource(R.menu.menu_bottom_navigation);
        navigation.setOnItemSelectedListener(this);
        navigation.setItemSelected(R.id.home, true);
    }


    @Override
    public void onItemSelected(int i) {
        Fragment fragment = null;
        if (i == R.id.profile) {
            fragment = new Profile(getApplicationContext(), AuthHelper.getLoggedUser());
        } else if (i == R.id.members) {
            fragment = new Members();
        } else if (i == R.id.home) {
            fragment = new Home(noticeSelected);
        } else if (i == R.id.admin) {
            fragment = new Admin();
        }
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}