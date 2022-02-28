package com.example.crudwithapi.remote;

public class APIUtils {
    private APIUtils(){
    };

    public static final String API_URL = "http://nugraha2-pc:86/api/Values/";
    //public static final String API_URL = "http://192.168.43.239:86/api/Values/";

    public static EmployeeService getEmployeeService1(){
        return RetrofitClient.getClient(API_URL).create(EmployeeService.class);
    }
}
