package com.example.crudwithapi.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.crudwithapi.R;
import com.example.crudwithapi.model.usernotification;
import com.example.crudwithapi.preference.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ForegroundServiceWorkers extends Worker {

    private PreferenceManager prefManager;
    private Context myContext;
    private String title;
    private NotificationManager notificationManager;

    private String channelId;

    private NotificationChannel channel;

    private Uri defaultSoundUri;

    public ForegroundServiceWorkers (@NonNull Context context, @NonNull WorkerParameters workerParams ) {
        super (context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork () {
        //call methods to perform background task
        myContext = this.getApplicationContext();
        title = myContext.getString(R.string.app_name);
        channelId = "foreground_default_channel";
        notificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (notificationManager != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    channelId,
                    "Channel notification for donation",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);

            if (prefManager != null && prefManager.getMyID() != null && !prefManager.getMyID().equals("") && prefManager.getMyID().length() > 0) {
                NotificationDismissedReceiver nv = new NotificationDismissedReceiver();
                startOpenNotification();
            }
        }

        return Result.success ();
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
                                    .setDeleteIntent(createOnDismissedIntent(myContext, channelId, artist.getNotificationId(), prefManager.getMyID(), artist.getID(), artist.getMessage()));

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
