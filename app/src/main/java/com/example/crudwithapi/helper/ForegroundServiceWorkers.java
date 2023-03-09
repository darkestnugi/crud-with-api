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
public class ForegroundServiceWorkers extends Worker {

    private PreferenceManager prefManager;
    private Context myContext;
    private String title;

    private NotificationManager notificationManager;

    public ForegroundServiceWorkers (@NonNull Context context, @NonNull WorkerParameters workerParams ) {
        super (context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork () {
        //call methods to perform background task
        myContext = this.getApplicationContext();
        title = myContext.getString(R.string.app_name);
        notificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

        startOpenNotification();
        return Result.success ();
    }

    private void startOpenNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            prefManager = new PreferenceManager(myContext);

            Intent intent = new Intent(myContext, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(myContext, 0 , intent, PendingIntent.FLAG_IMMUTABLE | 0);

            String channelId = "employee_default_channel";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel employee readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Query databaseusernotification = FirebaseDatabase.getInstance().getReference("usernotification").orderByChild("userIDTo").equalTo(prefManager.getMyID()).limitToLast(10);
            databaseusernotification.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        //iterating through all the nodes
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            //getting artist
                            usernotification artist = postSnapshot.getValue(usernotification.class);
                            if (artist.getIsActive() && artist.getChannelId().equals(channelId)) {
                                String msgId = artist.getID();
                                String msg = artist.getMessage();

                                if (msgId != null && !msgId.equals("") && msgId.length() > 0) {
                                    notificationManager.cancel(3);

                                    NotificationCompat.Builder notification = new NotificationCompat.Builder(myContext, channelId)
                                            .setSmallIcon(R.mipmap.ic_launcher_round)
                                            .setContentTitle(title)
                                            .setContentText(msg)
                                            .setAutoCancel(true)
                                            .setSound(defaultSoundUri)
                                            .setDeleteIntent(createOnDismissedIntent(myContext, 3, channelId, prefManager.getMyID(), msgId, msg))
                                            .setContentIntent(pendingIntent);

                                    notificationManager.notify(3, notification.build());
                                }
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

    private PendingIntent createOnDismissedIntent(Context context, int notificationId, String channelId, String userId, String msgId, String msg) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("channelId", channelId);
        intent.putExtra("userId", userId);
        intent.putExtra("msgId", msgId);
        intent.putExtra("msg", msg);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel employee readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

            notificationManager.cancel(3);
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), notificationId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }
}
