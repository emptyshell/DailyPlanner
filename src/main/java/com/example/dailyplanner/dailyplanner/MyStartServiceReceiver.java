package com.example.dailyplanner.dailyplanner;

/**
 * Created by valentin on 12.12.2017.
 */

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

public class MyStartServiceReceiver extends BroadcastReceiver {

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RECEIVER", "STARTED");
        Util.scheduleJob(context);
    }
}