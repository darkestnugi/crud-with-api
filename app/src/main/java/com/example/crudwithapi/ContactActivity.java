package com.example.crudwithapi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crudwithapi.adapter.ContactAdapter;
import com.example.crudwithapi.helper.ContactDBHelper;
import com.example.crudwithapi.model.contact;
import com.example.crudwithapi.model.employee;
import com.example.crudwithapi.preference.PreferenceManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Row;
import android.os.Environment;
import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ContactActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    private static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    private EditText txtSearchContact;
    private Button btnAddContact;
    private Button btnGetContactList;
    private Button btnDownloadContactList;
    private ListView listContact;

    private List<contact> list_contact = new ArrayList<contact>();

    private ContactDBHelper contactDBHelper;

    private PreferenceManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Picasso myotherpicasso;
    private Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

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
            Intent intent = new Intent(ContactActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        contactDBHelper = new ContactDBHelper(ContactActivity.this, null, null, 1);

        setTitle("Contact");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtSearchContact = (EditText) findViewById(R.id.txtSearchContact);
        btnAddContact = (Button) findViewById(R.id.btnAddContact);
        btnGetContactList = (Button) findViewById(R.id.btnGetContactList);
        btnDownloadContactList = (Button) findViewById(R.id.btnDownloadContactList);
        listContact = (ListView) findViewById(R.id.listContact);

        if (listContact == null || listContact.getCount() == 0){
            getContactList("");
        }

        btnGetContactList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContactList(txtSearchContact.getText().toString());
            }
        });

        btnDownloadContactList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadContactList(txtSearchContact.getText().toString());
            }
        });

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, ContactDetailActivity.class);
                intent.putExtra("contact_name", "");
                intent.putExtra("contact_email", "");
                intent.putExtra("contact_phone", "");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            if (prefManager.getMyName() == null) {
                Intent intent = new Intent(ContactActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else {
                prefManager.removeAllPreference();

                Intent intent = new Intent(ContactActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(ContactActivity.this, MyProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_22){
            AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setMessage("Do you want to exit?");
            builder.setIcon(R.mipmap.ic_baseline_power_settings_new_24_round);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();

                    mAuth.signOut();
                    prefManager.removeAllPreference();

                    Intent intent = new Intent(ContactActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(ContactActivity.this, EmployeeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_12){
            Intent intent = new Intent(ContactActivity.this, EmployeeDetailActivity.class);
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
            Intent intent = new Intent(ContactActivity.this, ContactActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_14){
            Intent intent = new Intent(ContactActivity.this, ContactDetailActivity.class);
            intent.putExtra("contact_name", "");
            intent.putExtra("contact_email", "");
            intent.putExtra("contact_phone", "");
            startActivity(intent);
        }
        else if (id == 16908332){
            Intent intent = new Intent(ContactActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return true;
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK twice to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void getContactList(String txtSearch){
        list_contact.clear();

        contactDBHelper.onOpen();
        Cursor mycursor = contactDBHelper.getAllContact(txtSearch);
        if (mycursor != null && mycursor.moveToFirst()) {
            do {
                contact temp_contact = new contact(mycursor.getString(0), mycursor.getString(1), mycursor.getString(2), mycursor.getString(3));
                list_contact.add(temp_contact);
            } while (mycursor.moveToNext());
        }

        contactDBHelper.close();
        listContact.setAdapter(new ContactAdapter(ContactActivity.this, R.layout.list_contact, list_contact));
    }

    public void downloadContactList(String txtSearch){
        list_contact.clear();

        contactDBHelper.onOpen();
        Cursor mycursor = contactDBHelper.getAllContact(txtSearch);
        if (mycursor != null && mycursor.moveToFirst()) {
            do {
                contact temp_contact = new contact(mycursor.getString(0), mycursor.getString(1), mycursor.getString(2), mycursor.getString(3));
                list_contact.add(temp_contact);
            } while (mycursor.moveToNext());
        }

        contactDBHelper.close();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                generateExcel(ContactActivity.this, list_contact);
            }
        }, 1000);
    }

    private static boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }

    private static boolean isExternalStoragePublicDirectoryAvailable() {
        int sdk_id = Build.VERSION.SDK_INT;
        if (sdk_id >= 19) {
            String directory1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
            String directory2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

            File folder1 = new File(directory1);
            File folder2 = new File(directory2);

            if (!folder1.exists() && !folder2.exists()) {
                return false;
            } else {
                return true;
            }
        }
        else {
            String directory2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

            File folder2 = new File(directory2);

            if (!folder2.exists()) {
                return false;
            } else {
                return true;
            }
        }
    }

    private static boolean storeExcelInStorage(Context context, Workbook workbook, String fileName) {
        boolean isSuccess = true;
        String directory = "";

        int sdk_id = Build.VERSION.SDK_INT;
        if (sdk_id >= 19) {
            File mainFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
            if (mainFolder.exists()) {
                directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/CRUDWithAPI/";
                File folder = new File(directory);
                if (!folder.exists()) {
                    isSuccess = folder.mkdir();
                }
            } else {
                directory = Environment.getExternalStorageDirectory() + "/CRUDWithAPI/";
                File folder = new File(directory);
                if (!folder.exists()) {
                    isSuccess = folder.mkdir();
                }
            }
        }
        else {
            directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/CRUDWithAPI/";
            File folder = new File(directory);
            if (!folder.exists()) {
                isSuccess = folder.mkdir();
            }
        }

        if (isSuccess) {
            File file = new File(directory, fileName);
            if (file.exists()){
                isSuccess = file.delete();
            }

            if (isSuccess) {
                FileOutputStream fileOutputStream = null;

                try {
                    fileOutputStream = new FileOutputStream(file);
                    workbook.write(fileOutputStream);

                    Toast.makeText(context, "Writing file: " + file, Toast.LENGTH_SHORT).show();
                    isSuccess = true;
                } catch (IOException e) {
                    Toast.makeText(context, "Error writing Exception: " + e, Toast.LENGTH_SHORT).show();
                    isSuccess = false;
                } catch (Exception e) {
                    Toast.makeText(context, "Failed to save file due to Exception: " + e, Toast.LENGTH_SHORT).show();
                    isSuccess = false;
                } finally {
                    try {
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(context, "Failed to close file because Exception: " + ex, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Toast.makeText(context, "Failed to delete file: " + file, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(context, "Storage not available or read only: " + directory + ". Android: " + Build.VERSION.SDK_INT, Toast.LENGTH_SHORT).show();
        }

        return isSuccess;
    }

    private static void generateExcel(Context context, List<contact> list_contact) {
        if (!isExternalStoragePublicDirectoryAvailable()) {
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
        }
        else {
            Workbook workbook = new HSSFWorkbook();

            // Create a new sheet in a Workbook and assign a name to it
            String EXCEL_SHEET_NAME = "Contact";
            final Sheet sheet = workbook.createSheet(EXCEL_SHEET_NAME);

            Cell cellHeader = null;

            // Cell style for a cell
            CellStyle cellStyleHeader = workbook.createCellStyle();
            cellStyleHeader.setFillForegroundColor(HSSFColor.AQUA.index);
            cellStyleHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyleHeader.setAlignment(CellStyle.ALIGN_CENTER);

            //Header
            Row rowHeader = sheet.createRow(0);

            cellHeader = rowHeader.createCell(0);
            cellHeader.setCellValue("Name");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(1);
            cellHeader.setCellValue("Email");
            cellHeader.setCellStyle(cellStyleHeader);

            cellHeader = rowHeader.createCell(2);
            cellHeader.setCellValue("Phone");
            cellHeader.setCellStyle(cellStyleHeader);

            //Body
            for (int i = 0; i < list_contact.size(); i++) {
                Row rowBody = sheet.createRow(i + 1);

                Cell cellBody = null;

                cellBody = rowBody.createCell(0);
                cellBody.setCellValue(list_contact.get(i).getName());

                cellBody = rowBody.createCell(1);
                cellBody.setCellValue(list_contact.get(i).getEmail());

                cellBody = rowBody.createCell(2);
                cellBody.setCellValue(list_contact.get(i).getPhone());
            }

            String fileName = "Contact_List.xls";
            boolean isWorkbookWrittenIntoStorage = storeExcelInStorage(context, workbook, fileName);
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