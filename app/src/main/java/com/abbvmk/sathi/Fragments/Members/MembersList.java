package com.abbvmk.sathi.Fragments.Members;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.screens.ProfileViewer.ProfileViewer;

import java.util.ArrayList;

public class MembersList extends Fragment implements MembersListAdapter.MemberCardInterface {

    private Context mContext;
    private ArrayList<User> users;
    private MembersListAdapter adapter;

    public MembersList() {
        // Required empty public constructor
    }

    public MembersList(Context context, ArrayList<User> users) {
        mContext = context;
        this.users = users;
        adapter = new MembersListAdapter(mContext, users, this);
    }

    public MembersList(Context context, ArrayList<User> users, MembersListAdapter.MemberCardInterface memberSelectedInterface) {
        mContext = context;
        this.users = users;
        adapter = new MembersListAdapter(mContext, users, memberSelectedInterface);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_members_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.membersRecycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

    }

    public void setUsers(ArrayList<User> users) {
        ArrayList<User> temp = MainApplication.getUsers();
        this.users.clear();
        this.users.addAll(users);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void MemberCardClicked(User user) {
        if (mContext == null) return;
        Intent intent = new Intent(getActivity(), ProfileViewer.class);
        intent.putExtra("user", user);
        startActivity(intent);
        if (getActivity() == null) return;
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}