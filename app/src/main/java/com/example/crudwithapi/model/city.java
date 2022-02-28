package com.example.crudwithapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class city {
    @SerializedName("ID")
    @Expose
    private String ID;

    @SerializedName("ProvinceID")
    @Expose
    private String ProvinceID;

    @SerializedName("Code")
    @Expose
    private String Code;

    @SerializedName("Name")
    @Expose
    private String Name;

    @SerializedName("CreatedBy")
    @Expose
    private String CreatedBy;

    @SerializedName("CreatedIP")
    @Expose
    private String CreatedIP;

    @SerializedName("CreatedPosition")
    @Expose
    private String CreatedPosition;

    @SerializedName("CreatedDate")
    @Expose
    private String CreatedDate;

    @SerializedName("ModifiedBy")
    @Expose
    private String ModifiedBy;

    @SerializedName("ModifiedIP")
    @Expose
    private String ModifiedIP;

    @SerializedName("ModifiedPosition")
    @Expose
    private String ModifiedPosition;

    @SerializedName("ModifiedDate")
    @Expose
    private String ModifiedDate;

    @SerializedName("IsActive")
    @Expose
    private boolean IsActive;

    public city() {
    }

    public city(String ID, String ProvinceID, String Code, String Name,
                String CreatedBy, String CreatedIP, String CreatedPosition, String CreatedDate,
                String ModifiedBy, String ModifiedIP, String ModifiedPosition, String ModifiedDate,
                boolean IsActive) {
        this.ID = ID;
        this.ProvinceID = ProvinceID;
        this.Code = Code;
        this.Name = Name;

        this.CreatedBy = CreatedBy;
        this.CreatedIP = CreatedIP;
        this.CreatedPosition = CreatedPosition;
        this.CreatedDate = CreatedDate;

        this.ModifiedBy = ModifiedBy;
        this.ModifiedIP = ModifiedIP;
        this.ModifiedPosition = ModifiedPosition;
        this.ModifiedDate = ModifiedDate;

        this.IsActive = IsActive;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getProvinceID() {
        return ProvinceID;
    }

    public void setProvinceID(String ProvinceID) {
        this.ProvinceID = ProvinceID;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String CreatedBy) {
        this.CreatedBy = CreatedBy;
    }

    public String getCreatedIP() {
        return CreatedIP;
    }

    public void setCreatedIP(String CreatedIP) {
        this.CreatedIP = CreatedIP;
    }

    public String getCreatedPosition() {
        return CreatedPosition;
    }

    public void setCreatedPosition(String CreatedPosition) {
        this.CreatedPosition = CreatedPosition;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String CreatedDate) {
        this.CreatedDate = CreatedDate;
    }

    public String getModifiedBy() {
        return ModifiedBy;
    }

    public void setModifiedBy(String ModifiedBy) {
        this.ModifiedBy = ModifiedBy;
    }

    public String getModifiedIP() {
        return ModifiedIP;
    }

    public void setModifiedIP(String ModifiedIP) {
        this.ModifiedIP = ModifiedIP;
    }

    public String getModifiedPosition() {
        return ModifiedPosition;
    }

    public void setModifiedPosition(String ModifiedPosition) {
        this.ModifiedPosition = ModifiedPosition;
    }

    public String getModifiedDate() {
        return ModifiedDate;
    }

    public void setModifiedDate(String ModifiedDate) {
        this.ModifiedDate = ModifiedDate;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public void setIsActive(boolean IsActive) {
        this.IsActive = IsActive;
    }

    @Override
    public String toString() {
        return this.Name;            // What to display in the Spinner list.
    }
}
