package com.example.crudwithapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class usernotification {
    @SerializedName("ID")
    @Expose
    private String ID;

    @SerializedName("UserIDFrom")
    @Expose
    private String UserIDFrom;

    @SerializedName("UserIDTo")
    @Expose
    private String UserIDTo;

    @SerializedName("NotificationId")
    @Expose
    private String NotificationId;

    @SerializedName("ChannelId")
    @Expose
    private String ChannelId;

    @SerializedName("Message")
    @Expose
    private String Message;

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
    
    public usernotification() {}

    public usernotification(String ID, String UserIDFrom, String UserIDTo, String NotificationId, String ChannelId, String Message,
                        String CreatedBy, String CreatedIP, String CreatedPosition, String CreatedDate,
                        String ModifiedBy, String ModifiedIP, String ModifiedPosition, String ModifiedDate,
                        String LastLoginIP, String LastLoginPosition, String LastLoginDate,
                        boolean IsActive) {
        this.ID = ID;
        this.UserIDFrom = UserIDFrom;
        this.UserIDTo = UserIDTo;
        this.NotificationId = NotificationId;
        this.ChannelId = ChannelId;
        this.Message = Message;

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

    public String getUserIDFrom() {
        return UserIDFrom;
    }

    public void setUserIDFrom(String UserIDFrom) {
        this.UserIDFrom = UserIDFrom;
    }

    public String getUserIDTo() {
        return UserIDTo;
    }

    public void setUserIDTo(String UserIDTo) {
        this.UserIDTo = UserIDTo;
    }

    public String getNotificationId() {
        return NotificationId;
    }

    public void setNotificationId(String NotificationId) {
        this.NotificationId = NotificationId;
    }

    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String ChannelId) {
        this.ChannelId = ChannelId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
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
