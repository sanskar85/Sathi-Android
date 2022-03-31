package com.abbvmk.sathi.screens.Admin.Designation;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.Views.ProgressButton.ProgressButton;

import java.util.ArrayList;

public class CreateDesignation extends AppCompatActivity implements ProgressButton.OnClickListener {
    private ProgressButton save;
    private ArrayList<String> designations;
    private DesignationAdapter adapter;
    private EditText designationET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_create_designation);
        save = findViewById(R.id.submit_btn);
        designationET = findViewById(R.id.designationET);
        save.setText("Create");
        save.setOnClickListener(this);
        RecyclerView designationRecycler = findViewById(R.id.designationRecycler);
        designations = new ArrayList<>();
        adapter = new DesignationAdapter(designations);
        designationRecycler.setHasFixedSize(false);
        designationRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        designationRecycler.setAdapter(adapter);

        fetchDesignations();

    }

    private void fetchDesignations() {
        Firebase
                .fetchDesignations(strings -> {
                    if (strings != null) {
                        designations.clear();
                        designations.addAll(strings);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to fetch designations", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick() {
        if (!save.isViewEnabled()) return;
        String title = designationET.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getApplicationContext(), "Designation cannot be empty", Toast.LENGTH_SHORT).show();
        }
        save.setViewEnabled(false);
        save.buttonActivated();

        Firebase
                .createDesignations(title, success -> {
                    if (success) {
                        designations.add(title);
                        adapter.notifyItemInserted(designations.size() - 1);
                        save.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.success));
                        save.buttonFinished("Created");
                        new Handler().postDelayed(() -> {
                            save.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.progress_bar_background));
                            save.buttonFinished("Create Another");
                            save.setViewEnabled(true);
                        }, 1000);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to create designation", Toast.LENGTH_SHORT).show();
                        save.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.failure));
                        save.buttonFinished("Failure");
                        new Handler().postDelayed(() -> {
                            save.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.progress_bar_background));
                            save.buttonFinished("Create");
                            save.setViewEnabled(true);
                        }, 3000);
                    }
                });

    }
}