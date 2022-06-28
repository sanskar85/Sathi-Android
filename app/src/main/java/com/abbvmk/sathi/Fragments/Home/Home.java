package com.abbvmk.sathi.Fragments.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.abbvmk.sathi.R;
import com.abbvmk.sathi.screens.CreatePost.CreatePost;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Home extends Fragment {
    private final boolean noticeSelected;

    public Home() {
        // Required empty public constructor
        noticeSelected = false;
    }

    public Home(boolean noticeSelected) {
        // Required empty public constructor
        this.noticeSelected = noticeSelected;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() == null || getActivity() == null) return;
        Activity activity = getActivity();
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.white));

        (view.findViewById(R.id.create_post)).setOnClickListener(v -> {
            if (getActivity() == null) return;
            Intent intent = new Intent(getActivity(), CreatePost.class);
            startActivity(intent);
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        ViewPager2 viewPager = view.findViewById(R.id.pager);
        HomeTabsAdapter adapter = new HomeTabsAdapter(this);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        if (noticeSelected) {
            viewPager.setCurrentItem(3, false);
        } else {
            viewPager.setCurrentItem(2, false);
        }
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    String title = null;
                    if (position == 0) {
                        title = "Admin";
                    } else if (position == 1) {
                        title = "Purpose";
                    } else if (position == 2) {
                        title = "New Posts";
                    } else if (position == 3) {
                        title = "Notice";
                    } else if (position == 4) {
                        title = "Committee Posts";
                    }
                    tab.setText(title);
                }
        ).attach();

    }
}