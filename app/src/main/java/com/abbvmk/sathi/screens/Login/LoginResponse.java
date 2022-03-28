package com.abbvmk.sathi.screens.Login;

import com.abbvmk.sathi.User.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class LoginResponse {
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("auth_token")
    @Expose
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
