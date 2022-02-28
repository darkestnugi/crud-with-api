package com.example.crudwithapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class employee {
    @SerializedName("ID")
    @Expose
    private String ID;

    @SerializedName("NIK")
    @Expose
    private String NIK;

    @SerializedName("NIP")
    @Expose
    private String NIP;

    @SerializedName("Email")
    @Expose
    private String Email;

    @SerializedName("Name")
    @Expose
    private String Name;

    @SerializedName("Password")
    @Expose
    private String Password;

    @SerializedName("PasswordKey")
    @Expose
    private String PasswordKey;

    @SerializedName("PositionID")
    @Expose
    private String PositionID;

    @SerializedName("PositionName")
    @Expose
    private String PositionName;

    @SerializedName("OfficeID")
    @Expose
    private String OfficeID;

    @SerializedName("OfficeName")
    @Expose
    private String OfficeName;

    @SerializedName("Salary")
    @Expose
    private double Salary;

    @SerializedName("Photo")
    @Expose
    private String Photo;

    @SerializedName("PhotoURL")
    @Expose
    private String PhotoURL;

    @SerializedName("ProvinceID")
    @Expose
    private String ProvinceID;

    @SerializedName("ProvinceName")
    @Expose
    private String ProvinceName;

    @SerializedName("CityID")
    @Expose
    private String CityID;

    @SerializedName("CityName")
    @Expose
    private String CityName;

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

    @SerializedName("LastLoginIP")
    @Expose
    private String LastLoginIP;

    @SerializedName("LastLoginPosition")
    @Expose
    private String LastLoginPosition;

    @SerializedName("LastLoginDate")
    @Expose
    private String LastLoginDate;

    @SerializedName("IsActive")
    @Expose
    private boolean IsActive;

    public employee() {
    }

    public employee(String ID, String NIK, String NIP, String Email, String Name,
                    String Password, String PasswordKey,
                    String PositionID, String PositionName, String OfficeID, String OfficeName,
                    double Salary, String Photo, String PhotoURL,
                    String ProvinceID, String ProvinceName, String CityID, String CityName,
                    String CreatedBy, String CreatedIP, String CreatedPosition, String CreatedDate,
                    String ModifiedBy, String ModifiedIP, String ModifiedPosition, String ModifiedDate,
                    String LastLoginIP, String LastLoginPosition, String LastLoginDate,
                    boolean IsActive) {

        this.ID = ID;
        this.NIK = NIK;
        this.NIP = NIP;
        this.Email = Email;
        this.Name = Name;
        this.Password = Password;
        this.PasswordKey = PasswordKey;

        this.PositionID = PositionID;
        this.PositionName = PositionName;
        this.OfficeID = OfficeID;
        this.OfficeName = OfficeName;

        this.Salary = Salary;
        this.Photo = Photo;
        this.PhotoURL = PhotoURL;

        this.ProvinceID = ProvinceID;
        this.ProvinceName = ProvinceName;
        this.CityID = CityID;
        this.CityName = CityName;

        this.CreatedBy = CreatedBy;
        this.CreatedIP = CreatedIP;
        this.CreatedPosition = CreatedPosition;
        this.CreatedDate = CreatedDate;

        this.ModifiedBy = ModifiedBy;
        this.ModifiedIP = ModifiedIP;
        this.ModifiedPosition = ModifiedPosition;
        this.ModifiedDate = ModifiedDate;

        this.LastLoginIP = LastLoginIP;
        this.LastLoginPosition = LastLoginPosition;
        this.LastLoginDate = LastLoginDate;

        this.IsActive = IsActive;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNIK() {
        return NIK;
    }

    public void setNIK(String NIK) {
        this.NIK = NIK;
    }

    public String getNIP() {
        return NIP;
    }

    public void setNIP(String NIP) {
        this.NIP = NIP;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getPasswordKey() {
        return PasswordKey;
    }

    public void setPasswordKey(String PasswordKey) {
        this.PasswordKey = PasswordKey;
    }

    public String getPositionID() {
        return PositionID;
    }

    public void setPositionID(String PositionID) {
        this.PositionID = PositionID;
    }

    public String getPositionName() {
        return PositionName;
    }

    public void setPositionName(String PositionName) {
        this.PositionName = PositionName;
    }

    public String getOfficeID() {
        return OfficeID;
    }

    public void setOfficeID(String OfficeID) {
        this.OfficeID = OfficeID;
    }

    public String getOfficeName() {
        return OfficeName;
    }

    public void setOfficeName(String OfficeName) {
        this.OfficeName = OfficeName;
    }

    public double getSalary() {
        return Salary;
    }

    public void setSalary(double Salary) {
        this.Salary = Salary;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String Photo) {
        this.Photo = Photo;
    }

    public String getPhotoURL() {
        return PhotoURL;
    }

    public void setPhotoURL(String PhotoURL) {
        this.PhotoURL = PhotoURL;
    }

    public String getProvinceID() {
        return ProvinceID;
    }

    public void setProvinceID(String ProvinceID) {
        this.ProvinceID = ProvinceID;
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String ProvinceName) {
        this.ProvinceName = ProvinceName;
    }

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String CityID) {
        this.CityID = CityID;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String CityName) {
        this.CityName = CityName;
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

    public String getLastLoginIP() {
        return LastLoginIP;
    }

    public void setLastLoginIP(String LastLoginIP) {
        this.LastLoginIP = LastLoginIP;
    }

    public String getLastLoginPosition() {
        return LastLoginPosition;
    }

    public void setLastLoginPosition(String LastLoginPosition) {
        this.LastLoginPosition = LastLoginPosition;
    }

    public String getLastLoginDate() {
        return LastLoginDate;
    }

    public void setLastLoginDate(String LastLoginDate) {
        this.LastLoginDate = LastLoginDate;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public void setIsActive(boolean IsActive) {
        this.IsActive = IsActive;
    }
}
