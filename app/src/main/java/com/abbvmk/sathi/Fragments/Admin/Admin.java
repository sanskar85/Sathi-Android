package com.abbvmk.sathi.Fragments.Admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.screens.Admin.Designation.AssignDesignation;
import com.abbvmk.sathi.screens.Admin.Designation.CreateDesignation;
import com.abbvmk.sathi.screens.Admin.Designation.PendingDesignation;
import com.abbvmk.sathi.screens.Admin.Notice.CreateNotice;

public class Admin extends Fragment implements View.OnClickListener {

    public Admin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AuthHelper.isAdmin()) {
            if (getActivity() != null)
                getActivity().finish();
            else
                Toast.makeText(getContext(), "You are not a committee member. You will not be able to alter settings.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false);
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


        User user = AuthHelper.getLoggedUser();
        if (user == null) {
            if (getActivity() != null) {
                getActivity().finish();
            }
            return;
        }
        boolean organisers = user.getDesignation().equals("अध्यक्ष") ||
                user.getDesignation().equals("सचिव") ||
                user.getDesignation().equals("उपाध्यक्ष");
        if (organisers) {
            view.findViewById(R.id.create_new_designation).setOnClickListener(this);
            view.findViewById(R.id.assign_designation).setOnClickListener(this);
            view.findViewById(R.id.pending_designation).setOnClickListener(this);
        } else {
            view.findViewById(R.id.create_new_designation).setVisibility(View.GONE);
            view.findViewById(R.id.assign_designation).setVisibility(View.GONE);
            view.findViewById(R.id.pending_designation).setVisibility(View.GONE);
        }
        view.findViewById(R.id.create_notice).setOnClickListener(this);
        view.findViewById(R.id.create_meeting).setOnClickListener(this);
        view.findViewById(R.id.create_meeting).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        if (view.getId() == R.id.create_new_designation) {
            intent = new Intent(view.getContext(), CreateDesignation.class);
        } else if (view.getId() == R.id.assign_designation) {
            intent = new Intent(view.getContext(), AssignDesignation.class);
        } else if (view.getId() == R.id.create_notice) {
            intent = new Intent(view.getContext(), CreateNotice.class);
        } else if (view.getId() == R.id.pending_designation) {
            intent = new Intent(view.getContext(), PendingDesignation.class);
        } else if (view.getId() == R.id.create_meeting) {
        }
        if (intent == null) return;
        startActivity(intent);
        if (getActivity() != null)
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
}