package com.example.crudwithapi.helper;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

@RequiresApi(api = Build.VERSION_CODES.N)
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            OneTimeWorkRequest request = new OneTimeWorkRequest
                    .Builder(ForegroundServiceWorkers.class)
                    .addTag("FOREGROUND_WORKER_TAG")
                    .build ();

            WorkManager.getInstance (this).enqueue ( request );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ForegroundServices.class));
        } else {
            startService(new Intent(this, ForegroundServices.class));
        }
    }
}
