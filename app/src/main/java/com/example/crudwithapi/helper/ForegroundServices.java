package com.example.crudwithapi.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.crudwithapi.MainActivity;
import com.example.crudwithapi.R;
import com.example.crudwithapi.model.usernotification;
import com.example.crudwithapi.preference.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ForegroundServices  extends Service {
    private PreferenceManager prefManager;
    Context myContext;
    private String title;
    private NotificationManager notificationManager;

    private String channelId;

    private NotificationChannel channel;

    private Uri defaultSoundUri;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // do your jobs here
        myContext = this.getApplicationContext();
        prefManager = new PreferenceManager(myContext);
        title = myContext.getString(R.string.app_name);
        channelId = "foreground_default_channel";
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

            channel = new NotificationChannel(
                    channelId,
                    "Channel notification for donation",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);

            NotificationDismissedReceiver nv = new NotificationDismissedReceiver();
            startForegroundService();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE | 0);

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Aplikasi CRUD API sedang berjalan")
                .setContentIntent(pendingIntent)
                .build();

        startOpenNotification();
        startForeground(1, notification);
    }

    private void startOpenNotification() {
        Query databaseusernotification = FirebaseDatabase.getInstance().getReference("usernotification").orderByChild("isActive").equalTo(true).limitToLast(65536);
        databaseusernotification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    //iterating through all the nodes
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //getting artist
                        usernotification artist = postSnapshot.getValue(usernotification.class);
                        if (artist.getIsActive() && artist.getChannelId().equals(channelId) && artist.getUserIDTo().equals(prefManager.getMyID())) {
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(myContext, channelId)
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle("Notifikasi nomor '" + artist.getNotificationId() + "'")
                                    .setContentText(artist.getMessage())
                                    .setSound(defaultSoundUri)
                                    .setDeleteIntent(createOnDismissedIntent(myContext, channelId, String.valueOf(artist.getNotificationId()), prefManager.getMyID(), artist.getID(), artist.getMessage()));

                            notificationManager.notify(Integer.parseInt(artist.getNotificationId()), notification.build());
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

    private PendingIntent createOnDismissedIntent(Context context, String channelId, String notificationId, String userId, String msgId, String msg) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("channelId", channelId);
        intent.putExtra("userId", userId);
        intent.putExtra("msgId", msgId);
        intent.putExtra("msg", msg);

        //Toast.makeText(myContext, channelId + " " + notificationId + " " + userId + " " + msgId + " " + msg, Toast.LENGTH_SHORT).show();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), Integer.parseInt(notificationId), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }
}
