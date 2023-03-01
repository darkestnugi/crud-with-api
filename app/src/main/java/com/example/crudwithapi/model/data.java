package com.example.crudwithapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class data {
    @SerializedName("body")
    @Expose
    private String body;

    public data() {}

    public String getbody() {
        return body;
    }

    public void setbody(String body) {
        this.body = body;
    }
}
