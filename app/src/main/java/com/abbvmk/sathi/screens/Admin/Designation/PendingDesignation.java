package com.abbvmk.sathi.screens.Admin.Designation;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.MainApplication;
import com.abbvmk.sathi.R;

import java.util.ArrayList;

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
        Firebase
                .fetchPendingDesignationUpdate(_pendingDesignation -> {
                    if (_pendingDesignation != null) {
                        pendingDesignations.clear();
                        pendingDesignations.addAll(_pendingDesignation);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(PendingDesignation.this, "Unable to fetch pending tasks", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void reject(PendingDesignationClass object) {
        Firebase
                .rejectPendingRequest(object.getId(), success -> {
                    if (success) {
                        Toast.makeText(PendingDesignation.this, "Rejected", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PendingDesignation.this, "Unable to reject the request", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void success(PendingDesignationClass object) {
        Firebase
                .approvePendingRequest(object.getId(), success -> {
                    if (success) {
                        MainApplication.fetchUsers();
                        Toast.makeText(PendingDesignation.this, "Approved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PendingDesignation.this, "Unable to approve the request", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}