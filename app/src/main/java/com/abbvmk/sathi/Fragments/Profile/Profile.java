package com.abbvmk.sathi.Fragments.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.abbvmk.sathi.Helper.API;
import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.Helper.FilesHelper;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.Views.Loading.Loading;
import com.abbvmk.sathi.screens.EditProfile.EditProfile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends Fragment implements FilesHelper.FileResponse {

    private User user;
    private String userID;

    private Context mContext;
    ShapeableImageView dp;
    private TextView name, relationName;
    private View edit_profile_btn;
    private Loading loading;

    public Profile() {
    }

    public Profile(Context context, User user) {
        mContext = context;
        this.user = user;
    }

    public Profile(Context context, String userID) {
        mContext = context;
        this.userID = userID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mContext == null || getActivity() == null) return;

        loading = view.findViewById(R.id.loading);

        Activity activity = getActivity();
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.light_sky_blue));

        dp = view.findViewById(R.id.dp);
        name = view.findViewById(R.id.nameTV);
        relationName = view.findViewById(R.id.relationTV);
        edit_profile_btn = view.findViewById(R.id.edit_profile_btn);

        if (user != null) {
            resolveData();
        } else if (userID != null) {
            fetchProfile();
        }
    }

    private void resolveData() {
        loading.setProgressVisible(false);
        userID = null;
        View view = getView();
        if (view == null) return;
        ViewPager2 viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(new ProfileTabsAdapter(this, user));

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    String title = null;
                    if (position == 0) {
                        title = "ID Card";
                    } else if (position == 1) {
                        title = "Details";
                    } else if (position == 2) {
                        title = "Posts";
                    }
                    tab.setText(title);
                }
        ).attach();
        edit_profile_btn.setVisibility(View.GONE);

        if (AuthHelper.getLoggedUser() != null && user.getId().equals(AuthHelper.getLoggedUser().getId())) {
            edit_profile_btn.setVisibility(View.VISIBLE);
            edit_profile_btn.setOnClickListener((v) -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), EditProfile.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }

        name.setText(user.getName());
        relationName.setText(String.format("%s %s", user.getRelationType(), user.getRelationName()));

        File file = FilesHelper.dp(mContext, user.getId());
        if (file != null) {
            loadImage(file);
        }
        FilesHelper.downloadDP(mContext, user.getId(), this);


    }

    private void fetchProfile() {
        loading.setProgressVisible(true);
        new Thread(() -> {
            API
                    .instance()
                    .fetchProfile(userID)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                            if (response.code() == 200) {
                                user = response.body();
                                resolveData();
                            } else {
                                Toast.makeText(getContext(), "Unable to fetch profile", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                            Toast.makeText(getContext(), "Unable to fetch profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        }).start();
    }


    @Override
    public void onFileDownloaded(File file) {
        loadImage(file);

    }

    private void loadImage(File file) {
        if (file != null) {
            Glide
                    .with(mContext.getApplicationContext())
                    .load(file)
                    .centerCrop()
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(dp);
        }
    }

}
