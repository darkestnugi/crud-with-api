package com.example.crudwithapi.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

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
    private PreferenceManager prefManager;

    @Override
    public void onReceive(Context myContext, Intent myIntent) {
        prefManager = new PreferenceManager(myContext);

        String notificationId = myIntent.getExtras().getString("notificationId");
        String channelId = myIntent.getExtras().getString("channelId");
        String userId = myIntent.getExtras().getString("userId");
        String msgId = myIntent.getExtras().getString("msgId");
        String msg = myIntent.getExtras().getString("msg");

        //Toast.makeText(myContext, channelId + " " + notificationId + " " + userId + " " + msgId + " " + msg, Toast.LENGTH_SHORT).show();

        Query databaseusernotification = FirebaseDatabase.getInstance().getReference("usernotification").orderByChild("isActive").equalTo(true).limitToLast(65536);
        databaseusernotification.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        usernotification artist = postSnapshot.getValue(usernotification.class);
                        if (artist.getIsActive() && artist.getChannelId().equals(channelId) && artist.getNotificationId().equals(notificationId) && artist.getUserIDTo().equals(prefManager.getMyID())) {
                            DatabaseReference dbusernotification = FirebaseDatabase.getInstance()
                                    .getReference("usernotification");

                            String mydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                            String myip = prefManager.getLocalIpAddress(myContext);

                            artist.setModifiedBy(prefManager.getMyID());
                            artist.setModifiedIP(myip);
                            artist.setModifiedPosition("home");
                            artist.setModifiedDate(mydate);
                            artist.setIsActive(false);

                            dbusernotification
                                    .child(artist.getID())
                                    .setValue(artist);

                            Toast.makeText(myContext, "Notifikasi nomor " + artist.getNotificationId() + " telah ditutup", Toast.LENGTH_SHORT).show();
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
