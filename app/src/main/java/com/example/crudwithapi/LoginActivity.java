package com.example.crudwithapi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crudwithapi.model.employee;
import com.example.crudwithapi.preference.PreferenceManager;
import com.example.crudwithapi.remote.APIUtils;
import com.example.crudwithapi.remote.EmployeeService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginActivity extends AppCompatActivity {
    private  static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    private  static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    private EditText edtUEmail;
    private EditText edtUPass;
    private TextView txtUError;

    private Button btnULogin;

    private List<employee> list_employee = new ArrayList<employee>();
    private employee temp_employee;

    private EmployeeService employeeService;
    private PreferenceManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Picasso myotherpicasso;
    private Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        myContext = this;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(myContext);
        prefManager = new PreferenceManager(this);

        setTitle("Login To CRUD API");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        edtUEmail = (EditText) findViewById(R.id.edtUEmail);
        edtUPass = (EditText) findViewById(R.id.edtUPass);
        txtUError = (TextView) findViewById(R.id.txtUError);
        btnULogin = (Button) findViewById(R.id.btnULogin);

        employeeService = APIUtils.getEmployeeService1();

        btnULogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEmployeeLoginValidation(edtUEmail.getText().toString(), edtUPass.getText().toString());
            }
        });
    }

    public void getEmployeeLoginValidation(final String email, final String password){
        boolean correct = true;
        String errorMsg = "";

        if (email == null || email.trim().length() <= 0 || email.trim().length() < 6){
            correct = false;
            edtUEmail.setError("incorrect email!");
        }

        if (password == null || password.trim().length() <= 0 || password.trim().length() < 6){
            correct = false;
            edtUPass.setError("incorrect password!");
        }

        if (!correct) {
            txtUError.setText(errorMsg);
        }
        else{
            if (getString(R.string.is_use_api).equals("Yes")) {
                Call<List<employee>> call = employeeService.getEmployee(email);
                call.enqueue(new Callback<List<employee>>() {
                    @Override
                    public void onResponse(Call<List<employee>> call, Response<List<employee>> response) {
                        if(response.isSuccessful()){
                            list_employee.clear();
                            list_employee = response.body();

                            for(int i = 0; i < list_employee.size(); i++){
                                temp_employee = list_employee.get(i);

                                String temp_email1 = temp_employee.getEmail();
                                String temp_pass1 = temp_employee.getPassword() + temp_employee.getPasswordKey();
                                String temp_pass2 = password + temp_employee.getPasswordKey();

                                if (temp_email1 == email && temp_pass1 == temp_pass2){
                                    break;
                                }
                            }

                            if (temp_employee != null){
                                String uniqueID = UUID.randomUUID().toString();
                                prefManager.setMyToken(uniqueID);
                                prefManager.setMyID(uniqueID);
                                prefManager.setMyName(email);
                                prefManager.setMyEmail(email);

                                updateloginemployee(temp_employee.getID(), temp_employee);
                                Toast.makeText(LoginActivity.this, "Selamat datang, " + temp_employee.getName(), Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Error: user tidak ditemukan!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<employee>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    String uniqueID = UUID.randomUUID().toString();
                                    prefManager.setMyToken(uniqueID);
                                    prefManager.setMyID(user.getUid());
                                    prefManager.setMyName(email);
                                    prefManager.setMyEmail(email);

                                    Toast.makeText(LoginActivity.this, "Selamat datang, " + email, Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Email atau Password tidak ditemukan!", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error: user tidak ditemukan! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    public void updateloginemployee(String id, employee u){
        String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String myip = prefManager.getLocalIpAddress(LoginActivity.this);

        u.setID(id);
        u.setLastLoginDate(mydate);
        u.setLastLoginIP(myip);
        u.setLastLoginPosition("home");

        if (getString(R.string.is_use_api).equals("Yes")) {
            Call<employee> call = employeeService.updateLoginEmployee(id, u);
            call.enqueue(new Callback<employee>() {
                @Override
                public void onResponse(Call<employee> call, Response<employee> response) {
                    if (response.isSuccessful()) {
                        employee temp_data = response.body();
                        if (temp_data != null && temp_data.getName().indexOf("Error:") >= 0) {
                            Toast.makeText(LoginActivity.this, "Error:" + temp_data.getName(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Error:" + response.code() + " : " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<employee> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            //Do something?
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