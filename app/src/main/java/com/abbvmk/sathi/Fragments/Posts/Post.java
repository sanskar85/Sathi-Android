package com.abbvmk.sathi.Fragments.Posts;

import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.User.ChildDetail;
import com.abbvmk.sathi.User.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Post implements Serializable {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("caption")
    @Expose
    private String caption;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean canBeDeleted() {

        User me = AuthHelper.getLoggedUser();
        if (me == null) return false;
        else if (getUser().getId().equals(me.getId())) return true;
        else if (me.getDesignation().equals("अध्यक्ष")) return true;
        else if (me.getDesignation().equals("सचिव")) return true;
        else if (me.getDesignation().equals("उपाध्यक्ष")) return true;

        return false;

    }
}
