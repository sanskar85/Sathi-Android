package com.abbvmk.sathi.screens.Admin.Designation;


import java.io.Serializable;

public class PendingDesignationClass implements Serializable {

    private String id;
    private String requestedBy;
    private String requestedFor;
    private String designation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getRequestedFor() {
        return requestedFor;
    }

    public void setRequestedFor(String requestedFor) {
        this.requestedFor = requestedFor;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
