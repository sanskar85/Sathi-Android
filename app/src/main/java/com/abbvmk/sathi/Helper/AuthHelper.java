package com.abbvmk.sathi.Helper;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.abbvmk.sathi.User.User;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AuthHelper {

    private static final String SHARED_PREFERENCES_NAME = "LoggedUser";

    private static AuthHelper instance;
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        if (instance == null) {
            instance = new AuthHelper();
            try {
                String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                sharedPreferences = EncryptedSharedPreferences.create(
                        SHARED_PREFERENCES_NAME,
                        masterKeyAlias,
                        context,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } catch (Exception e) {
                e.printStackTrace();
                sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            }
        }
    }

    public static User getLoggedUser() {
        String text = sharedPreferences.getString("User", null);
        if (text == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(text, User.class);
    }

    public static boolean isLoggedIn() {
        return sharedPreferences.contains("auth_token");
    }

    public static String getNotificationToken() {
        return sharedPreferences.getString("notification_token", "");
    }

    public static void saveNotificationToken(String token) {
        sharedPreferences.edit().putString("notification_token", token).apply();
    }

    public static void saveAuthenticationToken(String authToken) {
        sharedPreferences.edit().putString("auth_token", authToken).apply();
    }

    public static String getAuthenticationToken() {
        return sharedPreferences.getString("auth_token", null);
    }

    public static void fetchUserProfile() {
        User user = getLoggedUser();
        if (user == null) return;
        API
                .instance()
                .fetchProfile(user.getId())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.code() == 200 && response.body() != null) {
                            saveUser(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

                    }
                });
    }

    public static void saveUser(User user) {
        user.setUploadPath(null);
        Gson gson = new Gson();
        String json = gson.toJson(user);
        sharedPreferences.edit().putString("User", json).apply();
    }

    public static boolean isAdmin() {
        User user = getLoggedUser();
        return user != null && user.isAdmin();
    }
}
