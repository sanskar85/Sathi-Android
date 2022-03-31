package com.abbvmk.sathi.User;


import java.io.Serializable;
import java.util.List;


public class User implements Serializable {

    private String id;
    private String designation;
    private List<ChildDetail> childDetails = null;
    private Integer memberId;
    private Integer v;
    private String about;
    private String address1;
    private String address2;
    private String address3;
    private String dob;
    private String gender;
    private String maritalStatus;
    private String name;
    private String occupation;
    private String photo;
    private Integer pincode;
    private String qualification;
    private String relationName;
    private String relationType;
    private String fname;
    private String mname;
    private String bloodGroup;
    private Integer childCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return memberId != null ? memberId : 0;
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
        } else if (designation == null || designation.length() == 0) {
            setDesignation("सदस्य");
        }
    }

    public boolean isAdmin() {
        return designation != null && !designation.equals("सदस्य");
    }
}
