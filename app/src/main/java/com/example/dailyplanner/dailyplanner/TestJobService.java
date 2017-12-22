package com.example.dailyplanner.dailyplanner;

/**
 * Created by valentin on 12.12.2017.
 */

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.List;
import java.util.Stack;

/**
 * JobService to be scheduled by the JobScheduler.
 * start another service
 */
public class TestJobService extends JobService {
    private static final String TAG = "SyncService";
    private static final int NOTIFICATION_ID = 3;
    public static Reminder reminder;
    public static Stack<Reminder> reminderStack;
    public static boolean FIRSTRUN = false;
    public static long currentTimerInteval, nextTimerInterval;
    String message;

    @Override
    public boolean onStartJob(JobParameters params) {
        message = findMessageByTime(currentTimerInteval);
        Log.d("SERVICE", "STARTED");
        if (!reminderStack.empty()) {
            reminder = reminderStack.pop();
            currentTimerInteval = reminder.getDateTime();
        }

        while (currentTimerInteval < System.currentTimeMillis()) {
            if (currentTimerInteval > System.currentTimeMillis()) break;
            if (!reminderStack.empty()) {
                Log.i("currentTime", Long.toString(currentTimerInteval));
                nextTimerInterval = reminderStack.peek().getDateTime();
                currentTimerInteval = nextTimerInterval;
                //Log.i("systemTime",Long.toString(System.currentTimeMillis()));
            } else {

                break;
            }
            reminder = reminderStack.pop();
        }
        Log.d("REMINDER", findMessageByTime(currentTimerInteval));
        if (FIRSTRUN) {
            processStartNotification(message, getResources().getString(R.string.app_name), R.drawable.ic_time);
        } else {
            FIRSTRUN = true;
        }
        if (!reminderStack.empty()) {
            if (reminderStack.peek().getDateTime() > System.currentTimeMillis()) {
                Intent notifyIntent = new Intent(this, MyStartServiceReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, currentTimerInteval, pendingIntent);
                Log.i("nextTime", Long.toString(currentTimerInteval));
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processStartNotification(String message, String title, int iconID) {
        // Do something. For example, fetch fresh data from backend to create a rich notification?
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = "my_channel_01";
        // The user-visible name of the channel.
        CharSequence name = getString(R.string.channel_name);
        // The user-visible description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);
        // The id of the channel.
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(iconID)
                        .setContentTitle(title)
                        .setContentText(message);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        mBuilder.setContentIntent(pendingIntent);
        //mBuilder.setDeleteIntent(MyReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public String findMessageByTime(long time) {
        List<Reminder> reminders = new ReminderDB(this).read();

        for (Reminder obj : reminders) {
            if (obj.getDateTime() == currentTimerInteval) {
                return obj.reminder;
            }
        }
        return "";
    }
}