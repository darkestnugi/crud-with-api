package com.example.crudwithapi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crudwithapi.helper.ContactDBHelper;
import com.example.crudwithapi.model.contact;
import com.example.crudwithapi.preference.PreferenceManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ContactDetailActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    private static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    private TextView txtUCId;
    private EditText edtUCId;
    private EditText edtUCName;
    private EditText edtUCEmail;
    private EditText edtUCPhone;

    private Button btnCSave;
    private Button btnCDel;

    private ContactDBHelper contactDBHelper;

    private PreferenceManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Picasso myotherpicasso;
    private Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

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

        if (prefManager.getMyID() == null){
            Intent intent = new Intent(ContactDetailActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        contactDBHelper = new ContactDBHelper(ContactDetailActivity.this, null, null, 1);

        setTitle("Detail");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtUCId = (TextView) findViewById(R.id.txtUCId);
        edtUCId = (EditText) findViewById(R.id.edtUCId);
        edtUCName = (EditText) findViewById(R.id.edtUCName);
        edtUCEmail = (EditText) findViewById(R.id.edtUCEmail);
        edtUCPhone = (EditText) findViewById(R.id.edtUCPhone);

        btnCSave = (Button) findViewById(R.id.btnCSave);
        btnCDel = (Button) findViewById(R.id.btnCDel);

        Bundle extras = getIntent().getExtras();
        final String contactId = extras.getString("contact_id");
        String contactName = extras.getString("contact_name");
        String contactEmail = extras.getString("contact_email");
        String contactPhone = extras.getString("contact_phone");

        edtUCId.setText(contactId);
        edtUCName.setText(contactName);
        edtUCEmail.setText(contactEmail);
        edtUCPhone.setText(contactPhone);

        if(contactId != null && contactId.trim().length() > 0 ){
            edtUCId.setFocusable(false);
        } else {
            txtUCId.setVisibility(View.INVISIBLE);
            edtUCId.setVisibility(View.INVISIBLE);
            btnCDel.setVisibility(View.INVISIBLE);
        }

        btnCSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact u = new contact();
                u.setName(edtUCName.getText().toString());
                u.setEmail(edtUCEmail.getText().toString());
                u.setPhone(edtUCPhone.getText().toString());

                contactDBHelper.onOpen();

                if(contactId != null && contactId.trim().length() > 0){
                    //update contact
                    long value = contactDBHelper.updateContact(contactId, u);
                    Toast.makeText(ContactDetailActivity.this, "Berhasil diperbaharui " + value + " baris", Toast.LENGTH_SHORT).show();
                } else {
                    //add contact
                    String myID = UUID.randomUUID().toString();
                    u.setID(myID);

                    long value = contactDBHelper.insertContact(u);
                    Toast.makeText(ContactDetailActivity.this, "Berhasil ditambah dengan baris : " + value, Toast.LENGTH_SHORT).show();
                }

                contactDBHelper.close();

                Intent intent = new Intent(ContactDetailActivity.this, ContactActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnCDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactDBHelper.onOpen();

                boolean result = contactDBHelper.deleteContact(contactId);
                if (result){
                    Toast.makeText(ContactDetailActivity.this, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ContactDetailActivity.this, "Gagal dihapus", Toast.LENGTH_SHORT).show();
                }

                contactDBHelper.close();

                Intent intent = new Intent(ContactDetailActivity.this, ContactActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            if (prefManager.getMyName() == null) {
                Intent intent = new Intent(ContactDetailActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else {
                prefManager.removeAllPreference();

                Intent intent = new Intent(ContactDetailActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(ContactDetailActivity.this, MyProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_22){
            AlertDialog.Builder builder = new AlertDialog.Builder(ContactDetailActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setMessage("Do you want to logout?");
            builder.setIcon(R.mipmap.ic_baseline_power_settings_new_24_round);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();

                    mAuth.signOut();
                    prefManager.removeAllPreference();

                    Intent intent = new Intent(ContactDetailActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(ContactDetailActivity.this, EmployeeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_12){
            Intent intent = new Intent(ContactDetailActivity.this, EmployeeDetailActivity.class);
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
            Intent intent = new Intent(ContactDetailActivity.this, ContactActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_14){
            Intent intent = new Intent(ContactDetailActivity.this, ContactDetailActivity.class);
            intent.putExtra("contact_name", "");
            intent.putExtra("contact_email", "");
            intent.putExtra("contact_phone", "");
            startActivity(intent);
        }
        else if (id == 16908332){
            Intent intent = new Intent(ContactDetailActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return true;
    }

    private void handleUncaughtException (Thread thread, Throwable e) {
        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}