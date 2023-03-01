package com.example.crudwithapi.helper;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.crudwithapi.EmployeeDetailActivity;
import com.example.crudwithapi.model.usernotification;
import com.example.crudwithapi.preference.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getExtras().getInt("notificationId");
        String channelId = intent.getExtras().getString("channelId");
        String userId = intent.getExtras().getString("userId");
        String msgId = intent.getExtras().getString("msgId");
        String msg = intent.getExtras().getString("msg");

        PreferenceManager prefManager = new PreferenceManager(context);
        DatabaseReference dbusernotification = FirebaseDatabase.getInstance()
                .getReference("usernotification");

        String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String myip = prefManager.getLocalIpAddress(context);

        usernotification u = new usernotification();
        u.setUserIDFrom(prefManager.getMyID());
        u.setUserIDTo(prefManager.getMyID());
        u.setChannelId(channelId);
        u.setNotificationId(String.valueOf(notificationId));
        u.setMessage(msg);
        u.setCreatedBy(prefManager.getMyName());
        u.setCreatedIP(myip);
        u.setCreatedPosition("home");
        u.setCreatedDate(mydate);
        u.setModifiedBy(prefManager.getMyName());
        u.setModifiedIP(myip);
        u.setModifiedPosition("home");
        u.setModifiedDate(mydate);
        u.setIsActive(false);

        u.setID(msgId);
        dbusernotification
                .child(msgId)
                .setValue(u);

        /* Your code to handle the event here */
        Toast.makeText(context, "message '" + msgId + "' is closed by '" + userId + "'!", Toast.LENGTH_SHORT).show();
    }
}