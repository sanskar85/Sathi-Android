package com.abbvmk.sathi.Fragments.Members;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.abbvmk.sathi.User.User;

import java.util.ArrayList;
import java.util.Iterator;

public class MembersTabsAdapter extends FragmentStateAdapter {
    private final Context mContext;
    private final ArrayList<User> users;
    private final ArrayList<User> usersFiltered;


    public MembersTabsAdapter(Fragment fragment, ArrayList<User> users) {
        super(fragment);
        mContext = fragment.getContext();
        this.users = users;
        usersFiltered = new ArrayList<>(users);
        for (Iterator<User> it = usersFiltered.iterator(); it.hasNext(); ) {
            if (!it.next().isAdmin()) {
                it.remove();
            }
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = null;
        if (position == 0) {
            fragment = new MembersList(mContext, users);
        } else if (position == 1) {
            fragment = new MembersList(mContext, usersFiltered);
        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
