package com.abbvmk.sathi.screens.Admin.Designation;


import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbvmk.sathi.Fragments.Members.MembersList;
import com.abbvmk.sathi.Fragments.Members.MembersListAdapter;
import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;

import java.util.ArrayList;
import java.util.Iterator;

public class AssignDesignation extends AppCompatActivity implements MembersListAdapter.MemberCardInterface {

    private MembersList members;
    private AppCompatEditText searchBar;
    private ArrayList<String> designations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_assign_designation);
        searchBar = findViewById(R.id.searchBar);
        designations = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        members = new MembersList(this, new ArrayList<>(MainApplication.getUsers()), this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.membersContainer, members)
                .commit();
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
                if (!TextUtils.isEmpty(text)) {
                    new Thread(() -> {

                        ArrayList<User> usersFiltered = new ArrayList<>(MainApplication.getUsers());
                        for (Iterator<User> it = usersFiltered.iterator(); it.hasNext(); ) {
                            User user = it.next();
                            if (!String.valueOf(user.getMemberId()).startsWith(text) &&
                                    !user.getName().toLowerCase().startsWith(text.toLowerCase())) {
                                it.remove();
                            }
                        }
                        members.setUsers(usersFiltered);
                    }).start();
                } else {
                    members.setUsers(new ArrayList<>(MainApplication.getUsers()));
                }
            }
        });
        fetchDesignations();
    }

    private void fetchDesignations() {
        Firebase
                .fetchDesignations(strings -> {
                    if (strings != null) {
                        designations.clear();
                        designations.addAll(strings);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to fetch designations", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void MemberCardClicked(User user) {
        if (designations == null) {
            Toast.makeText(this, "Designations not loaded yet, Please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        View customView = View.inflate(this, R.layout.designation_chooser, null);


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(customView);
        dialog.show();

        RecyclerView designationRecycler = customView.findViewById(R.id.designationRecycler);
        DesignationAdapter adapter = new DesignationAdapter(designations, selectedDesignation -> {
            dialog.dismiss();
            PendingDesignationClass pendingDesignation = new PendingDesignationClass();
            pendingDesignation.setDesignation(selectedDesignation);
            pendingDesignation.setRequestedFor(user.getId());
            Firebase
                    .requestDesignationUpdate(pendingDesignation, success -> {
                        if (success) {
                            Toast.makeText(getApplicationContext(), "Designation update request created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to update designation", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        designationRecycler.setHasFixedSize(true);
        designationRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        designationRecycler.setAdapter(adapter);
    }
}