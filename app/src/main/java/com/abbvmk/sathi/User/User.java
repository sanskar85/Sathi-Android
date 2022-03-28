package com.abbvmk.sathi.User;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("uploadPath")
    @Expose
    private String uploadPath;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("childDetails")
    @Expose
    private List<ChildDetail> childDetails = null;
    @SerializedName("member_id")
    @Expose
    private Integer memberId;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("address1")
    @Expose
    private String address1;
    @SerializedName("address2")
    @Expose
    private String address2;
    @SerializedName("address3")
    @Expose
    private String address3;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("marital_status")
    @Expose
    private String maritalStatus;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("occupation")
    @Expose
    private String occupation;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("pincode")
    @Expose
    private Integer pincode;
    @SerializedName("qualification")
    @Expose
    private String qualification;
    @SerializedName("relationName")
    @Expose
    private String relationName;
    @SerializedName("relationType")
    @Expose
    private String relationType;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("mname")
    @Expose
    private String mname;
    @SerializedName("bloodGroup")
    @Expose
    private String bloodGroup;
    @SerializedName("childCount")
    @Expose
    private Integer childCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public List<ChildDetail> getChildDetails() {
        return childDetails;
    }

    public void setChildDetails(List<ChildDetail> childDetails) {
        this.childDetails = childDetails;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getAbout() {
        return about != null ? about : "";
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getAddress1() {
        return address1 != null ? address1 : "";
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2 != null ? address2 : "";
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3 != null ? address3 : "";
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getDob() {
        return dob != null ? dob : "";
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender != null ? gender : "";
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus != null ? maritalStatus : "";
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOccupation() {
        return occupation != null ? occupation : "";
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getPincode() {
        return pincode != null ? pincode : 0;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    public String getQualification() {
        return qualification != null ? qualification : "";
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getRelationName() {
        return relationName != null ? relationName : "";
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationType() {
        return relationType != null ? relationType : "";
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public Integer getChildCount() {
        return childCount != null ? childCount : 0;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public String getFname() {
        return fname != null ? fname : "";
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname != null ? mname : "";
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getBloodGroup() {
        return bloodGroup != null ? bloodGroup : "";
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public void validate() throws UserValidationException {
        if (name == null || name.length() == 0) {
            throw new UserValidationException("Name cannot be empty");
        } else if (mname == null || mname.length() == 0) {
            throw new UserValidationException("Mother's name cannot be empty");
        } else if (fname == null || fname.length() == 0) {
            throw new UserValidationException("Father's name cannot be empty");
        } else if (relationType == null || relationType.length() == 0) {
            throw new UserValidationException("Relation Type cannot be empty");
        } else if (relationName == null || relationName.length() == 0) {
            throw new UserValidationException("Relation Name cannot be empty");
        } else if (gender == null || gender.length() == 0) {
            throw new UserValidationException("Gender  cannot be empty");
        } else if (dob == null || dob.length() == 0) {
            throw new UserValidationException("Date of Birth cannot be empty");
        } else if (maritalStatus == null || maritalStatus.length() == 0) {
            throw new UserValidationException("Marital Status cannot be empty");
        } else if (qualification == null || qualification.length() == 0) {
            throw new UserValidationException("Qualification cannot be empty");
        } else if (occupation == null || occupation.length() == 0) {
            throw new UserValidationException("Occupation cannot be empty");
        }
    }

    public boolean isAdmin() {
        return designation != null && !designation.equals("सदस्य");
    }
}
