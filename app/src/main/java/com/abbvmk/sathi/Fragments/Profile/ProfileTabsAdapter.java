package com.abbvmk.sathi.Fragments.Profile;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.abbvmk.sathi.Fragments.Posts.Posts;
import com.abbvmk.sathi.User.User;

public class ProfileTabsAdapter extends FragmentStateAdapter {
    private final User user;
    private final Context mContext;


    public ProfileTabsAdapter(Fragment fragment, User user) {
        super(fragment);
        mContext = fragment.getContext();
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = null;
        if (position == 0) {
            fragment = new IDCard(mContext, user);
        } else if (position == 1) {
            fragment = new Details(user);
        } else if (position == 2) {
            fragment = new Posts(user);
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
