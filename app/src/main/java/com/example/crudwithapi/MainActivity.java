package com.example.crudwithapi;
import com.example.crudwithapi.adapter.EmployeeAdapter;
import com.example.crudwithapi.helper.NotificationDismissedReceiver;
import com.example.crudwithapi.model.employee;
import com.example.crudwithapi.model.userfcmtoken;
import com.example.crudwithapi.model.usernotification;
import com.example.crudwithapi.preference.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 100;
    private static final int READ_EXTERNAL_STORAGE_CODE = 200;
    private static final int INTERNET = 300;
    private static final int ACCESS_FINE_LOCATION_CODE = 400;
    private static final int ACCESS_COARSE_LOCATION_CODE = 500;

    private PreferenceManager prefManager;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Picasso myotherpicasso;
    private Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        setTitle("Welcome, " + prefManager.getMyName());
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Fetching FCM registration token failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        if (token == null || token.equals("") || token.length() == 0) {
                            Toast.makeText(MainActivity.this, "Fetching FCM registration token empty", Toast.LENGTH_SHORT).show();
                        } else {
                            Query databaseToken = FirebaseDatabase.getInstance().getReference("userfcmtoken").orderByChild("userID").equalTo(prefManager.getMyID());
                            databaseToken.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long count = dataSnapshot.getChildrenCount();
                                    userfcmtoken currUser = new userfcmtoken();

                                    if (count > 0) {
                                        //iterating through all the nodes
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            //getting artist
                                            userfcmtoken artist = postSnapshot.getValue(userfcmtoken.class);
                                            if (artist.getToken().equals(token)) {
                                                currUser = artist;
                                            }
                                        }

                                        if (currUser.getToken() != null && !currUser.getToken().equals("") && currUser.getToken().length() > 0) {
                                            DatabaseReference dbuserfcmtoken = FirebaseDatabase.getInstance()
                                                    .getReference("userfcmtoken");

                                            String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                            String myip = prefManager.getLocalIpAddress(MainActivity.this);

                                            currUser.setModifiedBy(prefManager.getMyName());
                                            currUser.setModifiedPosition("home");
                                            currUser.setModifiedDate(mydate);
                                            currUser.setModifiedIP(myip);

                                            dbuserfcmtoken
                                                    .child(currUser.getID())
                                                    .setValue(currUser);

                                            prefManager.setMyFCMToken(token);
                                        } else {
                                            DatabaseReference dbuserfcmtoken = FirebaseDatabase.getInstance()
                                                    .getReference("userfcmtoken");

                                            String myID = dbuserfcmtoken.push().getKey();
                                            String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                            String myip = prefManager.getLocalIpAddress(MainActivity.this);

                                            userfcmtoken u = new userfcmtoken(
                                                    myID,
                                                    prefManager.getMyID(),
                                                    prefManager.getMyFCMToken(),
                                                    prefManager.getMyName(),
                                                    myip,
                                                    "home",
                                                    mydate,
                                                    prefManager.getMyName(),
                                                    myip,
                                                    "home",
                                                    mydate,
                                                    null,
                                                    null,
                                                    null,
                                                    true
                                            );

                                            u.setID(myID);
                                            dbuserfcmtoken
                                                    .child(myID)
                                                    .setValue(u);

                                            prefManager.setMyFCMToken(token);
                                        }
                                    } else {
                                        DatabaseReference dbuserfcmtoken = FirebaseDatabase.getInstance()
                                                .getReference("userfcmtoken");

                                        String myID = dbuserfcmtoken.push().getKey();
                                        String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                        String myip = prefManager.getLocalIpAddress(MainActivity.this);

                                        userfcmtoken u = new userfcmtoken(
                                                myID,
                                                prefManager.getMyID(),
                                                prefManager.getMyFCMToken(),
                                                prefManager.getMyName(),
                                                myip,
                                                "home",
                                                mydate,
                                                prefManager.getMyName(),
                                                myip,
                                                "home",
                                                mydate,
                                                null,
                                                null,
                                                null,
                                                true
                                        );

                                        u.setID(myID);
                                        dbuserfcmtoken
                                                .child(myID)
                                                .setValue(u);

                                        prefManager.setMyFCMToken(token);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else {
                prefManager.removeAllPreference();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }

        startOpenNotification();
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
            Intent intent = new Intent(MainActivity.this, MyProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_22){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setMessage("Do you want to logout?");
            builder.setIcon(R.mipmap.ic_baseline_power_settings_new_24_round);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();

                    mAuth.signOut();
                    prefManager.removeAllPreference();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(MainActivity.this, EmployeeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_12){
            Intent intent = new Intent(MainActivity.this, EmployeeDetailActivity.class);
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
            Intent intent = new Intent(MainActivity.this, ContactActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.sub_menu_14){
            Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
            intent.putExtra("contact_name", "");
            intent.putExtra("contact_email", "");
            intent.putExtra("contact_phone", "");
            startActivity(intent);
        }
        else if (id == 16908332){
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return true;
    }

    private void startOpenNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Context myContext = this;
            PreferenceManager prefManager = new PreferenceManager(myContext);

            Intent intent = new Intent(myContext, EmployeeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(myContext, 0 /* Request code */, intent, PendingIntent.FLAG_IMMUTABLE);

            String channelId = "employee_default_channel";
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel employee readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

            Query databaseusernotification = FirebaseDatabase.getInstance().getReference("usernotification").orderByChild("userIDTo").equalTo(prefManager.getMyID()).limitToLast(10);
            databaseusernotification.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String msgId = "";
                    String msg = "";

                    if (dataSnapshot.getChildrenCount() > 0) {
                        //iterating through all the nodes
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            //getting artist
                            usernotification artist = postSnapshot.getValue(usernotification.class);
                            if (artist.getIsActive() && artist.getChannelId().equals(channelId)) {
                                msgId = artist.getID();
                                msg = artist.getMessage();
                            }
                        }

                        if (msgId != null && !msgId.equals("") && msgId.length() > 0) {
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(myContext, channelId)
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle(getString(R.string.app_name))
                                    .setContentText(msg)
                                    .setAutoCancel(true)
                                    .setDeleteIntent(createOnDismissedIntent(myContext, 3, channelId, prefManager.getMyID(), msgId, msg))
                                    .setContentIntent(pendingIntent);

                            notificationManager.notify(3, notification.build());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Do something?
                }
            });
        }
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId, String channelId, String userId, String msgId, String msg) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("channelId", channelId);
        intent.putExtra("userId", userId);
        intent.putExtra("msgId", msgId);
        intent.putExtra("msg", msg);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
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

    private void handleUncaughtException (Thread thread, Throwable e) {
        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
