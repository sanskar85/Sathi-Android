package com.abbvmk.sathi.Fragments.Home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abbvmk.sathi.Fragments.Members.MembersList;
import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;

import java.util.ArrayList;
import java.util.Iterator;

public class Committee extends Fragment {
    private Context mContext;

    public Committee() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_committee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() == null) return;
        mContext = getContext().getApplicationContext();
        fetchUsers();
    }

    private void fetchUsers() {
        new Thread(() -> {
            ArrayList<User> users = new ArrayList<>(MainApplication.getUsers());
            for (Iterator<User> it = users.iterator(); it.hasNext(); ) {
                if (!it.next().isAdmin()) {
                    it.remove();
                }
            }
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new MembersList(mContext, users))
                    .commit();
        }).start();

    }


}