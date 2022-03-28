package com.abbvmk.sathi.screens.Admin.Designation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.abbvmk.sathi.Fragments.Members.MembersList;
import com.abbvmk.sathi.Helper.API;
import com.abbvmk.sathi.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingDesignation extends AppCompatActivity implements AssignDesignationAdapter.OnApprovalClicked {

    private AssignDesignationAdapter adapter;
    private ArrayList<PendingDesignationClass> pendingDesignations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_pending_designation);
        pendingDesignations = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.membersRecycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new AssignDesignationAdapter(pendingDesignations, this);
        recyclerView.setAdapter(adapter);

        fetchPending();
    }

    private void fetchPending() {
        new Thread(() -> {
            API
                    .instance()
                    .fetchPendingApprovals()
                    .enqueue(new Callback<ArrayList<PendingDesignationClass>>() {
                        @Override
                        public void onResponse(@NonNull Call<ArrayList<PendingDesignationClass>> call, @NonNull Response<ArrayList<PendingDesignationClass>> response) {
                            if (response.code() == 200 && response.body() != null) {
                                pendingDesignations.clear();
                                pendingDesignations.addAll(response.body());
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(PendingDesignation.this, "Unable to fetch pending tasks", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ArrayList<PendingDesignationClass>> call, @NonNull Throwable t) {
                            Toast.makeText(PendingDesignation.this, "Unable to fetch pending tasks", Toast.LENGTH_SHORT).show();
                        }
                    });
        }).start();
    }

    @Override
    public void reject(PendingDesignationClass object) {
        new Thread(() -> {
            API
                    .instance()
                    .rejectPendingRequest(object.getId())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.code() == 200) {
                                Toast.makeText(PendingDesignation.this, "Rejected", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(PendingDesignation.this, "Unable to reject the request", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Toast.makeText(PendingDesignation.this, "Unable to reject the request", Toast.LENGTH_SHORT).show();
                        }
                    });
        }).start();
    }

    @Override
    public void success(PendingDesignationClass object) {
        new Thread(() -> {
            API
                    .instance()
                    .approvePendingRequest(object.getId())
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.code() == 200) {
                                Toast.makeText(PendingDesignation.this, "Approved", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(PendingDesignation.this, "Unable to approve the request", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Toast.makeText(PendingDesignation.this, "Unable to approve the request", Toast.LENGTH_SHORT).show();
                        }
                    });
        }).start();
    }
}