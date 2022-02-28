package com.example.crudwithapi.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.crudwithapi.EmployeeDetailActivity;
import com.example.crudwithapi.R;
import com.example.crudwithapi.model.employee;
import com.example.crudwithapi.remote.BitMapTransform;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.List;
import android.net.Uri;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EmployeeAdapter extends ArrayAdapter<employee> {
    private static final int MAX_WIDTH = 50;
    private static final int MAX_HEIGHT = 50;

    int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

    public static final String API_URL = "http://nugraha2-pc:86/";
    public static final String imageUrl = "https://via.placeholder.com/500";

    private Context context;
    private List<employee> employees;
    Picasso mypicasso;

    public EmployeeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<employee> objects, Picasso myotherpicasso) {
        super(context, resource, objects);
        this.context = context;
        this.employees = objects;
        this.mypicasso = myotherpicasso;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_employee, parent, false);

        ImageView imgemployee = (ImageView) rowView.findViewById(R.id.imgEView);
        TextView txtemployeeId = (TextView) rowView.findViewById(R.id.txtEViewId);
        TextView txtemployeeNIK = (TextView) rowView.findViewById(R.id.txtEViewNIK);
        TextView txtemployeeNIP = (TextView) rowView.findViewById(R.id.txtEViewNIP);
        TextView txtemployeeEmail = (TextView) rowView.findViewById(R.id.txtEViewEmail);
        TextView txtemployeeName = (TextView) rowView.findViewById(R.id.txtEViewName);
        TextView txtemployeePositionName = (TextView) rowView.findViewById(R.id.txtEViewPositionName);
        TextView txtemployeeOfficeName = (TextView) rowView.findViewById(R.id.txtEViewOfficeName);
        TextView txtemployeeSalary = (TextView) rowView.findViewById(R.id.txtEViewSalary);
        TextView txtemployeePhoto = (TextView) rowView.findViewById(R.id.txtEViewPhoto);
        TextView txtemployeePhotoURL = (TextView) rowView.findViewById(R.id.txtEViewPhotoURL);
        TextView txtemployeeProvinceName = (TextView) rowView.findViewById(R.id.txtEProvinceName);
        TextView txtemployeeCityName = (TextView) rowView.findViewById(R.id.txtECityName);

        String employeeId = employees.get(pos).getID();
        String employeePhoto = employees.get(pos).getPhoto();

        try {
            if (context.getString(R.string.is_use_api).equals("Yes")) {
                if (employeePhoto != null && employeePhoto.trim().length() > 0) {
                    mypicasso
                            .load(API_URL + "Files/" + employeePhoto)
                            //.memoryPolicy(MemoryPolicy.NO_CACHE)
                            //.networkPolicy(NetworkPolicy.NO_CACHE)
                            .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                            .resize(size, size)
                            .centerInside()
                            .into(imgemployee);

                    txtemployeePhotoURL.setText("photo url: " + API_URL + "Files/" + employeePhoto);
                } else {
                    mypicasso
                            .load(imageUrl)
                            //.memoryPolicy(MemoryPolicy.NO_CACHE)
                            //.networkPolicy(NetworkPolicy.NO_CACHE)
                            .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                            .resize(size, size)
                            .centerInside()
                            .into(imgemployee);

                    txtemployeePhotoURL.setText("photo url: " + imageUrl);
                }
            }
            else {
                if (employeePhoto != null && employeePhoto.trim().length() > 0) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("employee/" + employeeId + "/" + employeePhoto);

                    storageReference
                        .getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                mypicasso
                                        .load(downloadUrl)
                                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                                        .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                                        .resize(size, size)
                                        .centerInside()
                                        .into(imgemployee);

                                txtemployeePhotoURL.setText("photo url: " + downloadUrl.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                mypicasso
                                        .load(imageUrl)
                                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                                        .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                                        .resize(size, size)
                                        .centerInside()
                                        .into(imgemployee);

                                txtemployeePhotoURL.setText("photo url: " + imageUrl);
                            }
                        });
                } else {
                    mypicasso
                            .load(imageUrl)
                            //.memoryPolicy(MemoryPolicy.NO_CACHE)
                            //.networkPolicy(NetworkPolicy.NO_CACHE)
                            .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                            .resize(size, size)
                            .centerInside()
                            .into(imgemployee);

                    txtemployeePhotoURL.setText("photo url: " + imageUrl);
                }
            }
        }
        catch(Exception e){
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        txtemployeeId.setText(String.format("id: %s", employees.get(pos).getID()));
        txtemployeeNIK.setText(String.format("nik: %s", employees.get(pos).getNIK()));
        txtemployeeNIP.setText(String.format("nip: %s", employees.get(pos).getNIP()));
        txtemployeeEmail.setText(String.format("email: %s", employees.get(pos).getEmail()));
        txtemployeeName.setText(String.format("name: %s", employees.get(pos).getName()));
        txtemployeePositionName.setText(String.format("position: %s", employees.get(pos).getPositionName()));
        txtemployeeOfficeName.setText(String.format("office: %s", employees.get(pos).getOfficeName()));
        txtemployeeSalary.setText(String.format("salary: %.2f", employees.get(pos).getSalary()));
        txtemployeePhoto.setText(String.format("photo: %s", employees.get(pos).getPhoto()));
        txtemployeePhotoURL.setText(String.format("photo url: %s", employees.get(pos).getPhotoURL()));
        txtemployeeProvinceName.setText(String.format("province: %s", employees.get(pos).getProvinceName()));
        txtemployeeCityName.setText(String.format("city: %s", employees.get(pos).getCityName()));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start Activity employee Form
                Intent intent = new Intent(context, EmployeeDetailActivity.class);
                intent.putExtra("employee_id", String.valueOf(employees.get(pos).getID()));
                intent.putExtra("employee_nik", employees.get(pos).getNIK());
                intent.putExtra("employee_nip", employees.get(pos).getNIP());
                intent.putExtra("employee_email", employees.get(pos).getEmail());
                intent.putExtra("employee_name", employees.get(pos).getName());
                intent.putExtra("employee_positionid", String.valueOf(employees.get(pos).getPositionID()));
                intent.putExtra("employee_officeid", String.valueOf(employees.get(pos).getOfficeID()));
                intent.putExtra("employee_salary", String.format("%.2f", employees.get(pos).getSalary()));

                intent.putExtra("employee_photo", employees.get(pos).getPhoto());
                intent.putExtra("employee_photo_url", employees.get(pos).getPhotoURL());

                intent.putExtra("employee_provinceid", String.valueOf(employees.get(pos).getProvinceID()));
                intent.putExtra("employee_cityid", String.valueOf(employees.get(pos).getCityID()));
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}