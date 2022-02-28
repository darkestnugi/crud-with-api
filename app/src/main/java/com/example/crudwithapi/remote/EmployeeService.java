package com.example.crudwithapi.remote;

import android.text.Editable;

import com.example.crudwithapi.model.employee;
import com.example.crudwithapi.model.office;
import com.example.crudwithapi.model.position;
import com.example.crudwithapi.model.province;
import com.example.crudwithapi.model.city;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;

public interface EmployeeService {
    @GET("Get")
    Call<List<employee>> getEmployee(@Query("txtSearch") String txtSearch);

    @GET("GetById")
    Call<employee> getEmployeeById(@Path("id") String id);

    @GET("GetProvince")
    Call<List<province>> getProvince(@Query("txtSearch") String txtSearch, @Query("id") String id);

    @GET("GetCity")
    Call<List<city>> getCity(@Query("txtSearch") String txtSearch, @Query("provinceId") String provinceId);

    @GET("GetPosition")
    Call<List<position>> getPosition(@Query("txtSearch") String txtSearch, @Query("id") String id);

    @GET("GetOffice")
    Call<List<office>> getOffice(@Query("txtSearch") String txtSearch, @Query("id") String id);

    @POST("Post/")
    Call<employee> addEmployee(@Body employee myEmployee);

    @PUT("Put/{id}")
    Call<employee> updateEmployee(@Path("id") String id, @Body employee myEmployee);

    @PUT("PutLogin/{id}")
    Call<employee> updateLoginEmployee(@Path("id") String id, @Body employee myEmployee);

    @DELETE("Delete/{id}")
    Call<employee> deleteEmployee(@Path("id") String id);

    @Multipart
    @POST("UploadFile/{id}")
    Call<ResponseBody> uploadFile(@Path("id") String id, @Part MultipartBody.Part file);

    @GET("DownloadFile")
    Call<ResponseBody> downloadFile(@Query("fileName") String fileName);
}
