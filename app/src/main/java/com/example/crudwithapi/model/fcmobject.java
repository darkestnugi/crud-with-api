package com.example.crudwithapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class fcmobject {
    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("notification")
    @Expose
    private notification notification;

    @SerializedName("data")
    @Expose
    private data data;

    public fcmobject() {}

    public fcmobject(String token, notification notification, data data) {
        this.token = token;
        this.notification = notification;
        this.data = data;
    }

    public String gettoken() {
        return token;
    }

    public void settoken(String token) {
        this.token = token;
    }

    public notification getnotification() {
        return notification;
    }

    public void setnotification(notification notification) {
        this.notification = notification;
    }

    public data getdata() {
        return data;
    }

    public void setdata(data data) {
        this.data = data;
    }
}

