package com.abbvmk.sathi.screens.Admin.Designation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.abbvmk.sathi.Fragments.Members.MembersListAdapter;
import com.abbvmk.sathi.Helper.API;
import com.abbvmk.sathi.R;
import com.abbvmk.sathi.Views.ProgressButton.ProgressButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        new Thread(() -> {

            API
                    .instance()
                    .fetchDesignations()
                    .enqueue(new Callback<ArrayList<String>>() {
                        @Override
                        public void onResponse(@NonNull Call<ArrayList<String>> call, @NonNull Response<ArrayList<String>> response) {
                            if (response.code() == 200 && response.body() != null) {
                                designations.clear();
                                designations.addAll(response.body());
                                adapter.notifyDataSetChanged();
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
    public void onClick() {
        if (!save.isViewEnabled()) return;
        String title = designationET.getText().toString();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getApplicationContext(), "Designation cannot be empty", Toast.LENGTH_SHORT).show();
        }
        save.setViewEnabled(false);
        save.buttonActivated();
        API
                .instance()
                .createDesignations(title)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        designations.add(title);
                        adapter.notifyItemInserted(designations.size() - 1);
                        if (response.code() == 200) {
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
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Toast.makeText(getApplicationContext(), "Unable to create designation", Toast.LENGTH_SHORT).show();
                        save.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.failure));
                        save.buttonFinished("Failure");
                        new Handler().postDelayed(() -> {
                            save.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.progress_bar_background));
                            save.buttonFinished("Save");
                            save.setViewEnabled(true);
                        }, 3000);
                    }
                });
    }
}