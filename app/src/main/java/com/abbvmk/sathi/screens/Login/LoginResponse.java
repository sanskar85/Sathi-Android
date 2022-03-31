package com.abbvmk.sathi.screens.Login;

import com.abbvmk.sathi.User.User;
public class LoginResponse {
    private User user;
    private String authToken;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
