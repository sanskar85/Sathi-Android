package com.abbvmk.sathi.Fragments.Posts;

import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.User.User;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Post implements Serializable {
    private String id;
    private String photo;
    private String user;
    private String caption;

    @ServerTimestamp
    private Date time;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String filename) {
        this.photo = filename;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
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
        else if (getUser().equals(me.getId())) return true;
        else if (me.getDesignation().equals("अध्यक्ष")) return true;
        else if (me.getDesignation().equals("सचिव")) return true;
        else if (me.getDesignation().equals("उपाध्यक्ष")) return true;

        return false;

    }
}
