package com.abbvmk.sathi.Fragments.Notice;


import com.abbvmk.sathi.Helper.AuthHelper;
import com.abbvmk.sathi.User.User;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Notice implements Serializable {
    private String id;
    private String file;
    private String message;
    private String user;

    @ServerTimestamp
    private Date time;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String filename) {
        this.file = filename;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
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
