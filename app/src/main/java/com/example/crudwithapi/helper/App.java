package com.example.crudwithapi.helper;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(this, ForegroundServices.class));
    }
}
