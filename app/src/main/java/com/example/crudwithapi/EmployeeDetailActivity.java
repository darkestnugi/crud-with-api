package com.example.crudwithapi;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crudwithapi.helper.NotificationDismissedReceiver;
import com.example.crudwithapi.model.city;
import com.example.crudwithapi.model.data;
import com.example.crudwithapi.model.employee;
import com.example.crudwithapi.model.fcmobject;
import com.example.crudwithapi.model.notification;
import com.example.crudwithapi.model.office;
import com.example.crudwithapi.model.position;
import com.example.crudwithapi.model.province;
import com.example.crudwithapi.model.usernotification;
import com.example.crudwithapi.preference.PreferenceManager;
import com.example.crudwithapi.remote.APIUtils;
import com.example.crudwithapi.remote.BitMapTransform;
import com.example.crudwithapi.remote.EmployeeService;
import com.example.crudwithapi.remote.FileUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.OnProgressListener;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.app.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EmployeeDetailActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final int SELECT_PICK = 1;

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    private static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;

    private int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));
    private int maxDownloadSize = MAX_WIDTH * MAX_HEIGHT;

    private static final String API_URL = "http://nugraha2-pc:86/";
    private static final String imageUrl = "https://via.placeholder.com/500";

    private boolean initializeProvince = true;
    private boolean initializeCity = true;

    private Uri fileUri;
    private String filePath;

    private ImageView imageUEDownload;
    private TextView txtUEId;
    private EditText edtUEId;
    private EditText edtUENIK;
    private EditText edtUENIP;
    private EditText edtUEEmail;
    private EditText edtUEName;
    private EditText edtUESalary;

    private EditText edtUEPhoto;
    private EditText edtUEPhotoURL;

    private Spinner spinnerUEPosition;
    private Spinner spinnerUEOffice;
    private Spinner spinnerUEProvince;
    private Spinner spinnerUECity;

    private List<position> list_position = new ArrayList<position>();
    private List<office> list_office = new ArrayList<office>();
    private List<province> list_province = new ArrayList<province>();
    private List<city> list_city = new ArrayList<city>();

    private Button btnESave;
    private Button btnEDel;
    private Button btnEUpload;

    private EmployeeService employeeService;
    private PreferenceManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Picasso myotherpicasso;
    private Context myContext;

    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    WRITE_EXTERNAL_STORAGE_CODE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    READ_EXTERNAL_STORAGE_CODE,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.INTERNET)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    INTERNET,
                    Manifest.permission.INTERNET);
        }

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    ACCESS_FINE_LOCATION_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.app_name),
                    ACCESS_COARSE_LOCATION_CODE,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(myContext);
        prefManager = new PreferenceManager(this);

        if (prefManager.getMyID() == null){
            Intent intent = new Intent(EmployeeDetailActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        setTitle("Detail");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageUEDownload = (ImageView) findViewById(R.id.imageUEDownload);
        txtUEId = (TextView) findViewById(R.id.txtUEId);
        edtUEId = (EditText) findViewById(R.id.edtUEId);
        edtUENIK = (EditText) findViewById(R.id.edtUENIK);
        edtUENIP = (EditText) findViewById(R.id.edtUENIP);
        edtUEEmail = (EditText) findViewById(R.id.edtUEEmail);
        edtUEName = (EditText) findViewById(R.id.edtUEName);
        edtUESalary = (EditText) findViewById(R.id.edtUESalary);

        edtUEPhoto = (EditText) findViewById(R.id.edtUEPhoto);
        edtUEPhotoURL = (EditText) findViewById(R.id.edtUEPhotoURL);

        spinnerUEPosition = (Spinner) findViewById(R.id.spinnerUEPosition);
        spinnerUEOffice = (Spinner) findViewById(R.id.spinnerUEOffice);
        spinnerUEProvince = (Spinner) findViewById(R.id.spinnerUEProvince);
        spinnerUECity = (Spinner) findViewById(R.id.spinnerUECity);

        btnESave = (Button) findViewById(R.id.btnESave);
        btnEDel = (Button) findViewById(R.id.btnEDel);
        btnEUpload = (Button) findViewById(R.id.btnEUpload);

        employeeService = APIUtils.getEmployeeService1();
        myotherpicasso = new Picasso.Builder(this)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Toast.makeText(EmployeeDetailActivity.this, "Load File Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).build();

        Bundle extras = getIntent().getExtras();
        final String employeeId = extras.getString("employee_id");
        String employeeNIK = extras.getString("employee_nik");
        String employeeNIP = extras.getString("employee_nip");
        String employeeEmail = extras.getString("employee_email");
        String employeeName = extras.getString("employee_name");
        String employeePositionId = extras.getString("employee_positionid");
        String employeeOfficeId = extras.getString("employee_officeid");
        String employeeSalary = extras.getString("employee_salary");

        final String employeePhoto = extras.getString("employee_photo");
        final String employeePhotoURL = extras.getString("employee_photo_url");

        String employeeProvinceId = extras.getString("employee_provinceid");
        String employeeCityId = extras.getString("employee_cityid");

        edtUEId.setText(employeeId);
        edtUENIP.setText(employeeNIP);
        edtUENIK.setText(employeeNIK);
        edtUEEmail.setText(employeeEmail);
        edtUEName.setText(employeeName);
        edtUESalary.setText(employeeSalary);

        edtUEPhoto.setText(employeePhoto);
        edtUEPhotoURL.setText(employeePhotoURL);

        if (spinnerUEPosition.getCount() == 0){
            if (employeePositionId != null && employeePositionId.trim().length() == 0){
                getPositionList("", "");
            }
            else{
                getPositionList("", employeePositionId);
            }
        }

        if (spinnerUEOffice.getCount() == 0){
            if (employeeOfficeId != null && employeeOfficeId.trim().length() == 0){
                getOfficeList("", "");
            }
            else{
                getOfficeList("", employeeOfficeId);
            }
        }

        if (spinnerUEProvince.getCount() == 0){
            if (employeeProvinceId != null && employeeProvinceId.trim().length() == 0){
                getProvinceList("", "");
            }
            else{
                getProvinceList("", employeeProvinceId);
            }

            initializeProvince = true;
        }

        if (spinnerUECity.getCount() == 0){
            if (employeeCityId != null && employeeCityId.trim().length() == 0){
                getCityList("", "", "");
            }
            else{
                getCityList("", employeeProvinceId, employeeCityId);
            }

            initializeCity = true;
        }

        try {
            if (getString(R.string.is_use_api).equals("Yes")) {
                if (employeePhoto != null && employeePhoto.trim().length() > 0) {
                    myotherpicasso
                            .load(API_URL + "Files/" + employeePhoto)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                            .resize(size, size)
                            .centerInside()
                            .into(imageUEDownload);

                    edtUEPhotoURL.setText(API_URL + "Files/" + employeePhoto);
                } else {
                    myotherpicasso
                            .load(imageUrl)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                            .resize(size, size)
                            .centerInside()
                            .into(imageUEDownload);

                    edtUEPhotoURL.setText(imageUrl);
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
                                    myotherpicasso
                                            .load(downloadUrl)
                                            //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                            //.networkPolicy(NetworkPolicy.NO_CACHE)
                                            .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                                            .resize(size, size)
                                            .centerInside()
                                            .into(imageUEDownload);

                                    edtUEPhotoURL.setText(downloadUrl.toString());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    myotherpicasso
                                            .load(imageUrl)
                                            //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                            //.networkPolicy(NetworkPolicy.NO_CACHE)
                                            .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                                            .resize(size, size)
                                            .centerInside()
                                            .into(imageUEDownload);

                                    edtUEPhotoURL.setText(imageUrl);
                                }
                            });
                } else {
                    myotherpicasso
                            .load(imageUrl)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                            .resize(size, size)
                            .centerInside()
                            .into(imageUEDownload);

                    edtUEPhotoURL.setText(imageUrl);
                }
            }
        }
        catch(Exception e){
            Toast.makeText(EmployeeDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if(employeeId != null && employeeId.trim().length() > 0 ){
            edtUEId.setFocusable(false);
        } else {
            txtUEId.setVisibility(View.INVISIBLE);
            edtUEId.setVisibility(View.INVISIBLE);
            btnEDel.setVisibility(View.INVISIBLE);
        }

        edtUEPhoto.setFocusable(false);
        edtUEPhotoURL.setFocusable(false);

        spinnerUEProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                province myprovince = (province) parent.getSelectedItem();

                if (!initializeProvince){
                    getCityList("", myprovince.getID(), "");
                }

                initializeProvince = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerUECity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city mycity = (city) parent.getSelectedItem();

                if (!initializeCity){
                    //Do Something?
                }

                initializeCity = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnESave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                employee u = new employee();
                u.setNIK(edtUENIK.getText().toString());
                u.setNIP(edtUENIP.getText().toString());
                u.setEmail(edtUEEmail.getText().toString());
                u.setName(edtUEName.getText().toString());

                position myposition = (position)spinnerUEPosition.getSelectedItem();
                if (myposition != null) {
                    u.setPositionID(myposition.getID());
                    u.setPositionName(myposition.getName());
                }
                else{
                    u.setPositionID("");
                    u.setPositionName("");
                }

                office myoffice = (office)spinnerUEOffice.getSelectedItem();
                if (myoffice != null) {
                    u.setOfficeID(myoffice.getID());
                    u.setOfficeName(myoffice.getName());
                }
                else{
                    u.setOfficeID("");
                    u.setOfficeName("");
                }

                try{
                    u.setSalary(Double.parseDouble((edtUESalary.getText().toString().replace(',', '.'))));
                }
                catch (NumberFormatException e){
                    u.setSalary(0);
                }

                province myprovince = (province)spinnerUEProvince.getSelectedItem();
                if (myprovince != null) {
                    u.setProvinceID(myprovince.getID());
                    u.setProvinceName(myprovince.getName());
                }
                else{
                    u.setProvinceID("");
                    u.setProvinceName("");
                }

                city mycity = (city)spinnerUECity.getSelectedItem();
                if (mycity != null) {
                    u.setCityID(mycity.getID());
                    u.setCityName(mycity.getName());
                }
                else{
                    u.setCityID("");
                    u.setCityName("");
                }

                u.setPhoto(edtUEPhoto.getText().toString());
                u.setPhotoURL(edtUEPhotoURL.getText().toString());

                String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String myip = prefManager.getLocalIpAddress(EmployeeDetailActivity.this);

                if(employeeId != null && employeeId.trim().length() > 0){
                    //update employee
                    u.setModifiedBy(prefManager.getMyName());
                    u.setModifiedIP(myip);
                    u.setModifiedPosition("home");
                    u.setModifiedDate(mydate);
                    u.setIsActive(true);

                    updateemployee(employeeId, u);
                } else {
                    //add employee
                    u.setCreatedBy(prefManager.getMyName());
                    u.setCreatedIP(myip);
                    u.setCreatedPosition("home");
                    u.setCreatedDate(mydate);
                    u.setIsActive(true);

                    addemployee(u);
                }

                Intent intent = new Intent(EmployeeDetailActivity.this, EmployeeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnEDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeDetailActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Do you want to delete this employee?");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("employee/" + employeeId + "/" + employeePhoto);

                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deleteemployee(employeeId, employeeName);

                                Intent intent = new Intent(EmployeeDetailActivity.this, EmployeeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(EmployeeDetailActivity.this, "File delete failed. " + exception.getMessage() , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnEUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                //intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setAction(Intent.ACTION_PICK);

                Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
                intent.setDataAndType(uri, "image/*");

                //startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                startActivityForResult(intent, SELECT_PICK);
            }
        });

        imageUEDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String filename = edtUEPhoto.getText().toString();

                if (getString(R.string.is_use_api).equals("Yes")) {
                    Call<ResponseBody> call = employeeService.downloadFile(filename);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                ResponseBody body = response.body();
                                InputStream inputStream = body.byteStream();

                                String writtenToDisk = writeResponseBodyToDisk(filename, inputStream);
                                Toast.makeText(EmployeeDetailActivity.this, writtenToDisk, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EmployeeDetailActivity.this, "Error: File tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(EmployeeDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("employee/" + employeeId + "/" + filename);

                    storageReference.getBytes(maxDownloadSize).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            InputStream inputStream = new ByteArrayInputStream(bytes);

                            String writtenToDisk = writeResponseBodyToDisk(filename, inputStream);
                            Toast.makeText(EmployeeDetailActivity.this, writtenToDisk, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(EmployeeDetailActivity.this, "File downloaded failed. " + exception.getMessage() , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            if (prefManager.getMyName() == null) {
                Intent intent = new Intent(EmployeeDetailActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else {
                prefManager.removeAllPreference();

                Intent intent = new Intent(EmployeeDetailActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.mymenu, menu);

        MenuItem item1 = menu.findItem(R.id.item_menu_1);
        MenuItem item2 = menu.findItem(R.id.item_menu_2);

        item1.setVisible(true);
        item2.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.sub_menu_21){
            Intent intent = new Intent(EmployeeDetailActivity.this, MyProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_22){
            AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeDetailActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setMessage("Do you want to logout?");
            builder.setIcon(R.mipmap.ic_baseline_power_settings_new_24_round);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();

                    mAuth.signOut();
                    prefManager.removeAllPreference();

                    Intent intent = new Intent(EmployeeDetailActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else if (id == R.id.sub_menu_11){
            Intent intent = new Intent(EmployeeDetailActivity.this, EmployeeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_12){
            Intent intent = new Intent(EmployeeDetailActivity.this, EmployeeDetailActivity.class);
            intent.putExtra("employee_id", "");
            intent.putExtra("employee_nik", "");
            intent.putExtra("employee_nip", "");
            intent.putExtra("employee_email", "");
            intent.putExtra("employee_name", "");
            intent.putExtra("employee_positionid", "");
            intent.putExtra("employee_officeid", "");
            intent.putExtra("employee_salary", "");
            intent.putExtra("employee_photo", "");
            intent.putExtra("employee_provinceid", "");
            intent.putExtra("employee_cityid", "");
            startActivity(intent);
        }
        else if (id == R.id.sub_menu_13){
            Intent intent = new Intent(EmployeeDetailActivity.this, ContactActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_14){
            Intent intent = new Intent(EmployeeDetailActivity.this, ContactDetailActivity.class);
            intent.putExtra("contact_name", "");
            intent.putExtra("contact_email", "");
            intent.putExtra("contact_phone", "");
            startActivity(intent);
        }
        else if (id == 16908332){
            Intent intent = new Intent(EmployeeDetailActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == PICK_IMAGE) {
            fileUri = data.getData();
            File file = FileUtils.getFile(this, fileUri);

            if (fileUri != null && "content".equals(fileUri.getScheme())) {
                Cursor cursor = this.getContentResolver().query(fileUri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(0);
                cursor.close();
            } else {
                filePath = fileUri.getPath();
            }

            String employeeId = edtUEId.getText().toString().trim();
            if (file != null && employeeId != null && employeeId.length() > 0) {
                String filename = (String) DateFormat.format("yyyyMMdd_hhmmss_a", new Date());
                String extension = (API_URL + "Files/" + file.getName()).substring((API_URL + "Files/" + file.getName()).lastIndexOf("."));

                boolean result = uploadFile(employeeId, filename, extension, fileUri);
                if (result) {
                    edtUEPhoto.setText(filename + extension);
                }
            }
            else {
                Toast.makeText(EmployeeDetailActivity.this, "file not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addemployee(employee u){
        if (getString(R.string.is_use_api).equals("Yes")) {
            Call<employee> call = employeeService.addEmployee(u);
            call.enqueue(new Callback<employee>() {
                @Override
                public void onResponse(Call<employee> call, Response<employee> response) {
                    if (response.isSuccessful()) {
                        employee temp_data = response.body();
                        if (temp_data != null && temp_data.getName().indexOf("Error:") >= 0) {
                            Toast.makeText(EmployeeDetailActivity.this, "Error:" + temp_data.getName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EmployeeDetailActivity.this, "employee created successfully!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EmployeeDetailActivity.this, "Error:" + response.code() + " : " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<employee> call, Throwable t) {
                    Toast.makeText(EmployeeDetailActivity.this, "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            DatabaseReference dbemployee = FirebaseDatabase.getInstance()
                    .getReference("employee");

            String myID = dbemployee.push().getKey();

            u.setID(myID);
            dbemployee
                    .child(myID)
                    .setValue(u);

            addNotification("employee " + u.getName() + " created successfully!");

            Toast.makeText(EmployeeDetailActivity.this, "employee created successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateemployee(String id, employee u){
        if (getString(R.string.is_use_api).equals("Yes")) {
            u.setID(id);
            Call<employee> call = employeeService.updateEmployee(id, u);
            call.enqueue(new Callback<employee>() {
                @Override
                public void onResponse(Call<employee> call, Response<employee> response) {
                    if (response.isSuccessful()) {
                        employee temp_data = response.body();
                        if (temp_data != null && temp_data.getName().indexOf("Error:") >= 0) {
                            Toast.makeText(EmployeeDetailActivity.this, "Error:" + temp_data.getName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EmployeeDetailActivity.this, "employee updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EmployeeDetailActivity.this, "Error:" + response.code() + " : " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<employee> call, Throwable t) {
                    Toast.makeText(EmployeeDetailActivity.this, "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            DatabaseReference dbemployee = FirebaseDatabase.getInstance()
                    .getReference("employee");

            u.setID(id);
            dbemployee
                    .child(id)
                    .setValue(u);

            addNotification("employee " + u.getName() + " updated successfully!");

            Toast.makeText(EmployeeDetailActivity.this, "employee updated successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteemployee(String id, String name){
        if (getString(R.string.is_use_api).equals("Yes")) {
            Call<employee> call = employeeService.deleteEmployee(id);
            call.enqueue(new Callback<employee>() {
                @Override
                public void onResponse(Call<employee> call, Response<employee> response) {
                    if (response.isSuccessful()) {
                        employee temp_data = response.body();
                        if (temp_data != null && temp_data.getName().indexOf("Error:") >= 0) {
                            Toast.makeText(EmployeeDetailActivity.this, "Error:" + temp_data.getName(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EmployeeDetailActivity.this, "employee deleted successfully!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EmployeeDetailActivity.this, "Error:" + response.code() + " : " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<employee> call, Throwable t) {
                    Toast.makeText(EmployeeDetailActivity.this, "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            DatabaseReference dbemployee = FirebaseDatabase.getInstance()
                    .getReference("employee");

            dbemployee
                    .child(id)
                    .removeValue();

            addNotification("employee " + name + " deleted successfully!");

            Toast.makeText(EmployeeDetailActivity.this, "employee deleted successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNotification(String msg) {
        String channelId = "employee_default_channel";
        String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String myip = prefManager.getLocalIpAddress(EmployeeDetailActivity.this);

        DatabaseReference dbusernotification = FirebaseDatabase.getInstance()
                .getReference("usernotification");

        String myID = dbusernotification.push().getKey();

        usernotification u = new usernotification();
        u.setUserIDFrom(prefManager.getMyID());
        u.setUserIDTo(prefManager.getMyID());
        u.setChannelId(channelId);
        u.setNotificationId(String.valueOf(3));
        u.setMessage(msg);
        u.setCreatedBy(prefManager.getMyName());
        u.setCreatedIP(myip);
        u.setCreatedPosition("home");
        u.setCreatedDate(mydate);
        u.setIsActive(true);

        u.setID(myID);
        dbusernotification
                .child(myID)
                .setValue(u);

        Query databaseusernotification = FirebaseDatabase.getInstance().getReference("usernotification").orderByChild("userIDTo").equalTo(prefManager.getMyID()).limitToLast(1000);
        databaseusernotification.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        usernotification artist = postSnapshot.getValue(usernotification.class);
                        if (artist.getIsActive() && artist.getChannelId().equals(channelId) && artist.getNotificationId().equals("3")) {
                            DatabaseReference dbusernotification = FirebaseDatabase.getInstance()
                                    .getReference("usernotification");

                            artist.setChannelId(channelId);
                            artist.setNotificationId(String.valueOf(3));
                            artist.setMessage(msg);
                            artist.setModifiedBy(prefManager.getMyName());
                            artist.setModifiedIP(myip);
                            artist.setModifiedPosition("home");
                            artist.setModifiedDate(mydate);
                            artist.setIsActive(false);

                            dbusernotification
                                    .child(artist.getID())
                                    .setValue(artist);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do something?
            }
        });
    }
    private Task<String> addnotif(String title, String desc) throws JSONException {
        fcmobject input = new fcmobject();

        notification notification = new notification();
        notification.settitle(title);
        notification.setbody(desc);

        data data = new data();
        data.setbody(desc);

        input.settoken(prefManager.getMyFCMToken());
        input.setnotification(notification);
        input.setdata(data);

        // Create the arguments to the callable function.
        String jsonInString = new Gson().toJson(input);
        JSONObject mJSONObject = new JSONObject(jsonInString);

        return mFunctions
                .getHttpsCallable("testNotif")
                .call(mJSONObject)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        Toast.makeText(EmployeeDetailActivity.this, "Message: " + result, Toast.LENGTH_SHORT).show();
                        return result;
                    }
                });
    }

    public boolean uploadFile(String employeeId, String filename, String extension, Uri fileUri) {
        boolean result = true;

        if (getString(R.string.is_use_api).equals("Yes")) {
            try {
                File file = FileUtils.getFile(this, fileUri);

                if ((file.length() / 1024) >= 1024) {
                    Toast.makeText(EmployeeDetailActivity.this, "Error: maximum file size is 1024 Kb. Current size " + (file.length() / 1024) + " Kb", Toast.LENGTH_SHORT).show();
                    result = false;
                } else {
                    // create RequestBody instance from file
                    RequestBody requestFile =
                            RequestBody.create(
                                    MediaType.parse(getContentResolver().getType(fileUri)),
                                    file
                            );

                    // MultipartBody.Part is used to send also the actual file name
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("Picture", filename + extension, requestFile);

                    // add another part within the multipart request
                    String descriptionString = "This is description image";
                    RequestBody description =
                            RequestBody.create(
                                    okhttp3.MultipartBody.FORM, descriptionString);

                    // finally, execute the request
                    Call<ResponseBody> call = employeeService.uploadFile(employeeId, body);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Toast.makeText(EmployeeDetailActivity.this, "File uploaded succesfully", Toast.LENGTH_SHORT).show();

                            if (response.isSuccessful()) {
                                if ((filename + extension) != null && (filename + extension).trim().length() > 0) {
                                    myotherpicasso
                                            .load(API_URL + "Files/" + filename + extension)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                                            .resize(size, size)
                                            .centerInside()
                                            .into(imageUEDownload);

                                    edtUEPhoto.setText(filename + extension);
                                    edtUEPhotoURL.setText(API_URL + "Files/" + filename + extension);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(EmployeeDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                Toast.makeText(EmployeeDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                result = false;
            }
        }
        else {
            File file = FileUtils.getFile(this, fileUri);

            if ((file.length() / 1024) >= 1024) {
                Toast.makeText(EmployeeDetailActivity.this, "Error: maximum file size is 1024 Kb. Current size " + (file.length() / 1024) + " Kb", Toast.LENGTH_SHORT).show();
                result = false;
            } else {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference("employee/" + employeeId + "/" + filename + extension);

                storageReference.putFile(fileUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //if the upload is successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                storageReference
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri downloadUrl) {
                                                myotherpicasso
                                                        .load(downloadUrl)
                                                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                                                        .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                                                        .resize(size, size)
                                                        .centerInside()
                                                        .into(imageUEDownload);

                                                edtUEPhoto.setText(filename + extension);
                                                edtUEPhotoURL.setText(downloadUrl.toString());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                myotherpicasso
                                                        .load(imageUrl)
                                                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                                                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                                                        .transform(new BitMapTransform(MAX_WIDTH, MAX_HEIGHT))
                                                        .resize(size, size)
                                                        .centerInside()
                                                        .into(imageUEDownload);

                                                edtUEPhoto.setText(filename + extension);
                                                edtUEPhotoURL.setText(imageUrl);

                                                //and displaying error message
                                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                edtUEPhoto.setText(filename + extension);
                                edtUEPhotoURL.setText(imageUrl);

                                //and displaying error message
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                //displaying percentage in progress dialog
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        });
            }
        }

        return result;
    }

    public String writeResponseBodyToDisk(String filename, InputStream inputStream){
        try {
            boolean success = true;
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/CRUDWithAPI/";
            File folder = new File(directory);
            if (!folder.exists()){
                success = folder.mkdir();
            }

            if (success) {
                File futureStudioIconFile = new File(directory + filename);
                OutputStream outputStream = null;

                try {
                    byte[] fileReader = new byte[4096];
                    long fileSizeDownloaded = 0;
                    outputStream = new FileOutputStream(futureStudioIconFile);

                    while (true) {
                        int read = inputStream.read(fileReader);

                        if (read == -1) {
                            break;
                        }

                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                    }

                    outputStream.flush();

                    return "Berhasil disimpan di :" + directory + filename;
                } catch (IOException e) {
                    return e.getMessage();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            }
            else{
                return "Directory Penyimpanan gagal dibuat";
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public void getPositionList(String txtSearch, final String id){
        if (getString(R.string.is_use_api).equals("Yes")) {
            Call<List<position>> call = employeeService.getPosition(txtSearch, id);
            call.enqueue(new Callback<List<position>>() {
                @Override
                public void onResponse(Call<List<position>> call, Response<List<position>> response) {
                    if (response.isSuccessful()) {
                        list_position.clear();

                        List<position> temp_list = response.body();
                        position curr_position = new position();

                        for (int i = 0; i < temp_list.size(); i++) {
                            position temp_position = temp_list.get(i);

                            if (!id.equals("")) {
                                if (temp_position.getID().equals(id)) {
                                    curr_position = temp_position;
                                }
                            }

                            list_position.add(temp_position);
                        }

                        ArrayAdapter<position> adapter = new ArrayAdapter<position>(EmployeeDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, list_position);
                        spinnerUEPosition.setAdapter(adapter);

                        if (curr_position.getID() != null && !curr_position.getID().equals("")) {
                            int spinnerPosition = adapter.getPosition(curr_position);
                            spinnerUEPosition.setSelection(spinnerPosition);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<position>> call, Throwable t) {
                    Toast.makeText(EmployeeDetailActivity.this, "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            position curr_position = new position();

            for (int i = 0; i < 10; i++) {
                position temp_position = new position();
                temp_position.setID(String.valueOf(i));
                temp_position.setName("Position Name " + i);
                temp_position.setCode("Position Code " + i);

                if (!id.equals("")) {
                    if (temp_position.getID().equals(id)) {
                        curr_position = temp_position;
                    }
                }

                list_position.add(temp_position);
            }

            ArrayAdapter<position> adapter = new ArrayAdapter<position>(EmployeeDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, list_position);
            spinnerUEPosition.setAdapter(adapter);

            if (curr_position.getID() != null && !curr_position.getID().equals("")) {
                int spinnerPosition = adapter.getPosition(curr_position);
                spinnerUEPosition.setSelection(spinnerPosition);
            }
        }
    }

    public void getOfficeList(String txtSearch, final String id){
        if (getString(R.string.is_use_api).equals("Yes")) {
            Call<List<office>> call = employeeService.getOffice(txtSearch, id);
            call.enqueue(new Callback<List<office>>() {
                @Override
                public void onResponse(Call<List<office>> call, Response<List<office>> response) {
                    if(response.isSuccessful()) {
                        list_office.clear();

                        List<office> temp_list = response.body();
                        office curr_office = new office();

                        for (int i = 0; i < temp_list.size(); i++){
                            office temp_office = temp_list.get(i);

                            if (!id.equals("")){
                                if (temp_office.getID().equals(id)){
                                    curr_office = temp_office;
                                }
                            }

                            list_office.add(temp_office);
                        }

                        ArrayAdapter<office> adapter = new ArrayAdapter<office>(EmployeeDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, list_office);
                        spinnerUEOffice.setAdapter(adapter);

                        if (curr_office.getID() != null && !curr_office.getID().equals("")) {
                            int spinnerOffice = adapter.getPosition(curr_office);
                            spinnerUEOffice.setSelection(spinnerOffice);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<office>> call, Throwable t) {
                    Toast.makeText(EmployeeDetailActivity.this, "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            office curr_office = new office();

            for (int i = 0; i < 10; i++) {
                office temp_office = new office();
                temp_office.setID(String.valueOf(i));
                temp_office.setName("Office Name " + i);
                temp_office.setCode("Office Code " + i);

                if (!id.equals("")) {
                    if (temp_office.getID().equals(id)) {
                        curr_office = temp_office;
                    }
                }

                list_office.add(temp_office);
            }

            ArrayAdapter<office> adapter = new ArrayAdapter<office>(EmployeeDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, list_office);
            spinnerUEOffice.setAdapter(adapter);

            if (curr_office.getID() != null && !curr_office.getID().equals("")) {
                int spinnerOffice = adapter.getPosition(curr_office);
                spinnerUEOffice.setSelection(spinnerOffice);
            }
        }
    }

    public void getProvinceList(String txtSearch, final String id){
        if (getString(R.string.is_use_api).equals("Yes")) {
            Call<List<province>> call = employeeService.getProvince(txtSearch, id);
            call.enqueue(new Callback<List<province>>() {
                @Override
                public void onResponse(Call<List<province>> call, Response<List<province>> response) {
                    if (response.isSuccessful()) {
                        list_province.clear();
                        List<province> temp_list = response.body();
                        province curr_province = new province();

                        for (int i = 0; i < temp_list.size(); i++) {
                            province temp_province = temp_list.get(i);

                            if (!id.equals("")) {
                                if (temp_province.getID().equals(id)) {
                                    curr_province = temp_province;
                                }
                            }

                            list_province.add(temp_province);
                        }

                        ArrayAdapter<province> adapter = new ArrayAdapter<province>(EmployeeDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, list_province);
                        spinnerUEProvince.setAdapter(adapter);

                        if (curr_province.getID() != null && !curr_province.getID().equals("")) {
                            int spinnerPosition = adapter.getPosition(curr_province);
                            spinnerUEProvince.setSelection(spinnerPosition);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<province>> call, Throwable t) {
                    Toast.makeText(EmployeeDetailActivity.this, "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            list_province.clear();
            province curr_province = new province();

            for (int i = 0; i < 10; i++) {
                province temp_province = new province();
                temp_province.setID(String.valueOf(i));
                temp_province.setName("Province Name " + i);
                temp_province.setCode("Province Code " + i);

                if (!id.equals("")) {
                    if (temp_province.getID().equals(id)) {
                        curr_province = temp_province;
                    }
                }

                list_province.add(temp_province);
            }

            ArrayAdapter<province> adapter = new ArrayAdapter<province>(EmployeeDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, list_province);
            spinnerUEProvince.setAdapter(adapter);

            if (curr_province.getID() != null && !curr_province.getID().equals("")) {
                int spinnerProvince = adapter.getPosition(curr_province);
                spinnerUEProvince.setSelection(spinnerProvince);
            }
        }
    }

    public void getCityList(String txtSearch, final String provinceId, final String id){
        if (getString(R.string.is_use_api).equals("Yes")) {
            Call<List<city>> call = employeeService.getCity(txtSearch, provinceId);
            call.enqueue(new Callback<List<city>>() {
            @Override
            public void onResponse(Call<List<city>> call, Response<List<city>> response) {
                if(response.isSuccessful()){
                    list_city.clear();
                    List<city> temp_list = response.body();
                    city curr_city = new city();

                    for (int i = 0; i < temp_list.size(); i++){
                        city temp_city = temp_list.get(i);

                        if (!provinceId.equals("")) {
                            if (temp_city.getProvinceID().equals(provinceId)) {
                                if (!id.equals("")) {
                                    if (temp_city.getID().equals(id)) {
                                        curr_city = temp_city;
                                    }
                                }

                                list_city.add(temp_city);
                            }
                        }

                        list_city.add(temp_city);
                    }

                    ArrayAdapter<city> adapter = new ArrayAdapter<city>(EmployeeDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, list_city);
                    spinnerUECity.setAdapter(adapter);

                    if (curr_city.getID() != null && !curr_city.getID().equals("")) {
                        int spinnerCity = adapter.getPosition(curr_city);
                        spinnerUECity.setSelection(spinnerCity);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<city>> call, Throwable t) {
                Toast.makeText(EmployeeDetailActivity.this, "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        }
        else {
            list_city.clear();
            city curr_city = new city();
            
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    city temp_city = new city();
                    temp_city.setID(String.valueOf(j));
                    temp_city.setProvinceID(String.valueOf(i));
                    temp_city.setName("Province Name " + i + " City Name " + j);
                    temp_city.setCode("Province Name " + i + " City Code " + j);

                    if (!provinceId.equals("")){
                        if (temp_city.getProvinceID().equals(provinceId)){
                            if (!id.equals("")) {
                                if (temp_city.getID().equals(id)) {
                                    curr_city = temp_city;
                                }
                            }

                            list_city.add(temp_city);
                        }
                    }
                    else{
                        list_city.add(temp_city);
                    }
                }
            }
            
            ArrayAdapter<city> adapter = new ArrayAdapter<city>(EmployeeDetailActivity.this, android.R.layout.simple_spinner_dropdown_item, list_city);
            spinnerUECity.setAdapter(adapter);

            if (curr_city.getID() != null && !curr_city.getID().equals("")) {
                int spinnerCity = adapter.getPosition(curr_city);
                spinnerUECity.setSelection(spinnerCity);
            }
        }
    }

    private void handleUncaughtException (Thread thread, Throwable e) {
        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}