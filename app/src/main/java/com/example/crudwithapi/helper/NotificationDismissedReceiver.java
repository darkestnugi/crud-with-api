package com.example.crudwithapi.helper;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.crudwithapi.EmployeeDetailActivity;
import com.example.crudwithapi.model.usernotification;
import com.example.crudwithapi.preference.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.N)
public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            PreferenceManager prefManager = new PreferenceManager(context);
            int notificationId = intent.getExtras().getInt("notificationId");
            String channelId = intent.getExtras().getString("channelId");
            String userId = intent.getExtras().getString("userId");
            String msgId = intent.getExtras().getString("msgId");
            String msg = intent.getExtras().getString("msg");

            if (msgId != null && !msgId.equals("") && msgId.length() > 0) {
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

                                    String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                    String myip = prefManager.getLocalIpAddress(context);

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
    }
}