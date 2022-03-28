package com.abbvmk.sathi;

import android.app.Application;

import androidx.annotation.NonNull;

import com.abbvmk.sathi.Helper.API;
import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.User.User;
import com.google.firebase.FirebaseApp;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainApplication extends Application {
    private static ArrayList<User> users;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());
        AuthHelper.init(getApplicationContext());
        users = new ArrayList<>();
    }

    public static ArrayList<User> getUsers() {
        return users;
    }


    public static void fetchUsers() {
        new Thread(() -> {
            API
                    .instance()
                    .fetchAllUsers()
                    .enqueue(new Callback<ArrayList<User>>() {
                        @Override
                        public void onResponse(@NonNull Call<ArrayList<User>> call, @NonNull Response<ArrayList<User>> response) {
                            if (response.code() == 200 && response.body() != null) {
                                users = response.body();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ArrayList<User>> call, @NonNull Throwable t) {

                        }
                    });
        }).start();
    }
}