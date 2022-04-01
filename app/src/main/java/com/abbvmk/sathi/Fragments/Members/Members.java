package com.abbvmk.sathi.Fragments.Members;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Iterator;

public class Members extends Fragment {

    private Context mContext;
    private ArrayList<User> users;
    private ViewPager2 viewPager;
    private FrameLayout searchResultContainer;
    private TabLayout tabLayout;
    private AppBarLayout abbBarLayout;


    public Members() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_members, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() == null || getActivity() == null) return;
        mContext = getContext();
        users = new ArrayList<>(MainApplication.getUsers());

        Activity activity = getActivity();
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.light_sky_blue));


        abbBarLayout = view.findViewById(R.id.app_bar);
        AppCompatEditText searchBar = view.findViewById(R.id.searchBar);
        searchResultContainer = view.findViewById(R.id.searchResultContainer);
        viewPager = view.findViewById(R.id.pager);
        MembersTabsAdapter adapter = new MembersTabsAdapter(this, users);
        viewPager.setAdapter(adapter);
        tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    String title = null;
                    if (position == 0) {
                        title = "All Members";
                    } else if (position == 1) {
                        title = "Committee";
                    }
                    tab.setText(title);
                }
        ).attach();
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                new Thread(() -> {

                }).start();
                if (text.length() > 0) {
                    search(text);
                } else {
                    switchVisibility(false);
                }
            }
        });
    }

    private void search(String text) {
        new Thread(() -> {
            ArrayList<User> usersFiltered = new ArrayList<>(users);
            for (Iterator<User> it = usersFiltered.iterator(); it.hasNext(); ) {
                User user = it.next();
                if (!String.valueOf(user.getMemberId()).startsWith(text) &&
                        !user.getName().toLowerCase().startsWith(text.toLowerCase())) {
                    it.remove();
                }
            }
            if (searchResultContainer.getVisibility() == View.VISIBLE) {
                MembersList searchFrag = (MembersList) getChildFragmentManager().findFragmentById(R.id.searchResultContainer);
                if (searchFrag != null)
                    searchFrag.setUsers(usersFiltered);
            } else {
                FragmentActivity activity = Members.this.getActivity();
                if (activity == null) return;
                MembersList searchFrag = new MembersList(mContext, usersFiltered);
                activity.runOnUiThread(() -> {
                    switchVisibility(true);
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.searchResultContainer, searchFrag)
                            .commit();
                });

            }

        }).start();
    }

    private void switchVisibility(boolean searchFragVisible) {
        if (searchFragVisible) {
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            abbBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, (int) (mContext.getResources().getDisplayMetrics().density * 70)));
            searchResultContainer.setVisibility(View.VISIBLE);
        } else {
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            abbBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, (int) (mContext.getResources().getDisplayMetrics().density * 130)));
            searchResultContainer.setVisibility(View.GONE);
        }
    }


}