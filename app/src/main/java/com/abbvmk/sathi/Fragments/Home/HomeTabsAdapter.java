package com.abbvmk.sathi.Fragments.Home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.abbvmk.sathi.Fragments.Posts.Posts;
import com.abbvmk.sathi.Notice.NoticeList;

public class HomeTabsAdapter extends FragmentStateAdapter {

    public HomeTabsAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = null;
        if (position == 0) {
            fragment = new Committee();
        } else if (position == 1) {
            fragment = new Purpose();
        } else if (position == 2) {
            fragment = new Posts();
        } else if (position == 3) {
            fragment = new NoticeList();
        } else if (position == 4) {
            fragment = new Posts(true);
        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
