package com.abbvmk.sathi;

import android.app.Application;

import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.Helper.Firebase;
import com.abbvmk.sathi.User.User;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainApplication extends Application {
    private static ArrayList<User> users;
    private static Map<String, User> usersMap;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());
        AuthHelper.init(getApplicationContext());
        users = new ArrayList<>();
        usersMap = new HashMap<>();
    }

    public static ArrayList<User> getUsers() {
        return users;
    }


    public static void fetchUsers() {
        Firebase
                .fetchUsers(_users -> {
                    usersMap.clear();
                    users = _users;
                    for (User u : _users) {
                        usersMap.put(u.getId(), u);
                    }
                });
    }

    public static User findUser(String userID) {
        if (usersMap.containsKey(userID))
            return usersMap.get(userID);
        return null;
    }


}