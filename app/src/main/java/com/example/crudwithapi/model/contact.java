package com.example.crudwithapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class contact {
    @SerializedName("ID")
    @Expose
    private String ID;

    @SerializedName("Name")
    @Expose
    private String Name;

    @SerializedName("Email")
    @Expose
    private String Email;

    @SerializedName("Phone")
    @Expose
    private String Phone;

    public contact() {
    }

    public contact(String ID, String Name, String Email, String Phone) {
        this.ID = ID;
        this.Name = Name;
        this.Email = Email;
        this.Phone = Phone;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    @Override
    public String toString() {
        return this.Name;            // What to display in the Spinner list.
    }
}
