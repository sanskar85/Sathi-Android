package com.abbvmk.sathi.screens.Admin.Designation;

import com.abbvmk.sathi.User.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PendingDesignationClass implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("requestedBy")
    @Expose
    private User requestedBy;

    @SerializedName("requestedFor")
    @Expose
    private User requestedFor;

    @SerializedName("designation")
    @Expose
    private String designation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public User getRequestedFor() {
        return requestedFor;
    }

    public void setRequestedFor(User requestedFor) {
        this.requestedFor = requestedFor;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
