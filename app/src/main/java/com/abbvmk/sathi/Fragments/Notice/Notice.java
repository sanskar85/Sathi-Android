package com.abbvmk.sathi.Fragments.Notice;


import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.User.ChildDetail;
import com.abbvmk.sathi.User.User;
import com.abbvmk.sathi.User.UserValidationException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Notice implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("filename")
    @Expose
    private String filename;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("user")
    @Expose
    private User user;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean canBeDeleted() {

        User me = AuthHelper.getLoggedUser();
        if (me == null) return false;
        else if (me.getDesignation().equals("अध्यक्ष")) return true;
        else if (me.getDesignation().equals("सचिव")) return true;
        else if (me.getDesignation().equals("उपाध्यक्ष")) return true;

        return false;

    }
}
