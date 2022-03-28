package com.abbvmk.sathi.screens.Admin.Designation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.abbvmk.sathi.Fragments.Members.MembersList;
import com.abbvmk.sathi.Fragments.Members.MembersListAdapter;
import com.abbvmk.sathi.Helper.API;
import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.User.User;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignDesignation extends AppCompatActivity implements MembersListAdapter.MemberCardInterface {

    private MembersList members;
    private AppCompatEditText searchBar;
    private ArrayList<String> designations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_assign_designation);
        searchBar = findViewById(R.id.searchBar);
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
        new Thread(() -> {

            API
                    .instance()
                    .fetchDesignations()
                    .enqueue(new Callback<ArrayList<String>>() {
                        @Override
                        public void onResponse(@NonNull Call<ArrayList<String>> call, @NonNull Response<ArrayList<String>> response) {
                            if (response.code() == 200 && response.body() != null) {
                                designations = response.body();
                            } else {
                                Toast.makeText(getApplicationContext(), "Unable to fetch designations", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(@NonNull Call<ArrayList<String>> call, @NonNull Throwable t) {
                            Toast.makeText(getApplicationContext(), "Unable to fetch designations", Toast.LENGTH_SHORT).show();
                        }
                    });
        }).start();
    }

    @Override
    public void MemberCardClicked(User user) {
        if (designations == null) {
            Toast.makeText(this, "Designations not loaded yet, Please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        View customView = getLayoutInflater().inflate(R.layout.designation_chooser, null);


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(customView);
        dialog.show();

        RecyclerView designationRecycler = customView.findViewById(R.id.designationRecycler);
        DesignationAdapter adapter = new DesignationAdapter(designations, selectedDesignation -> {
            dialog.dismiss();
            new Thread(() -> {
                API
                        .instance()
                        .requestDesignationUpdate(user.getId(), selectedDesignation)
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.code() == 200) {
                                    Toast.makeText(getApplicationContext(), "Designation update request created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Unable to update designation", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Toast.makeText(getApplicationContext(), "Unable to update designation", Toast.LENGTH_SHORT).show();
                            }
                        });
            }).start();
        });
        designationRecycler.setHasFixedSize(true);
        designationRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        designationRecycler.setAdapter(adapter);
    }
}