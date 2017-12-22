package com.example.dailyplanner.dailyplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    long millis;
    ReminderDB reminderDB = new ReminderDB(this);

    public void initAHBottomNavigation() {
        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.addButtonText, R.drawable.ic_plus, R.color.addButtonColor);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.removeButtonText, R.drawable.ic_remove, R.color.removeButtonColor);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.updateButtonText, R.drawable.ic_update, R.color.updateButtonColor);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setColored(true);

    }

    public void setInitAHBottomNavigationListener() {

        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        intent = new Intent(MainActivity.this, AddActivity.class);
                        //Log.d("TEST", "I was here, hehe");
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, RemoveActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, UpdateActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TestJobService.FIRSTRUN = false;
        setContentView(R.layout.activity_main);
        intent = getIntent();
        initAHBottomNavigation();
        setInitAHBottomNavigationListener();
        int recordCount = reminderDB.countDB();

        TextView textViewRecordCount = (TextView) findViewById(R.id.textView);
        textViewRecordCount.setText(recordCount + " reminders found.");
        readRecords();

        Button button = (Button) findViewById(R.id.button);
        final EditText editText = (EditText) findViewById(R.id.editText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty()) {
                    readRecords();
                } else {
                    findView(editText.getText().toString());
                }

            }
        });


        List<Reminder> reminders = new ReminderDB(this).read();
        reminders = sortMyReminderList(reminders);
        Stack<Reminder> reminderStack = new Stack<>();
        if (reminders.size() > 0) {
            for (Reminder obj : reminders) {
                if (obj.getDateTime() > System.currentTimeMillis()) millis = obj.getDateTime();
                reminderStack.push(obj);
                //Log.d("LIST", obj.date+" "+obj.time);
            }
        }
        TestJobService.currentTimerInteval = millis;
        TestJobService.reminderStack = reminderStack;
        Log.d("StartingTime", Long.toString(millis));
        Intent notifyIntent = new Intent(this, MyStartServiceReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }

    public void readRecords() {

        LinearLayout linearLayoutRecords = (LinearLayout) findViewById(R.id.linearLayoutRecords);
        linearLayoutRecords.removeAllViews();

        List<Reminder> reminders = new ReminderDB(this).read();

        if (reminders.size() > 0) {
            linearLayoutRecords.removeAllViews();
            for (Reminder obj : reminders) {


                int id = obj.id;
                String reminder = obj.reminder;
                String time = obj.time;
                String date = obj.date;

                String textViewContents = Integer.toString(id) + " | " + reminder + " | " + time + " | " + date;

                TextView textViewReminderItem = new TextView(this);
                textViewReminderItem.setPadding(0, 10, 0, 10);
                textViewReminderItem.setText(textViewContents);
                textViewReminderItem.setTag(Integer.toString(id));
                textViewReminderItem.setTextSize(16);

                linearLayoutRecords.addView(textViewReminderItem);
            }

        } else {
            linearLayoutRecords.removeAllViews();
            TextView locationItem = new TextView(this);
            locationItem.setPadding(8, 8, 8, 8);
            locationItem.setText("No records yet.");

            linearLayoutRecords.addView(locationItem);
        }

    }

    void findView(String string) {
        List<Reminder> reminderList = new ReminderDB(this).find(string);
        LinearLayout linearLayoutRecords = (LinearLayout) findViewById(R.id.linearLayoutRecords);

        if (reminderList.size() > 0) {
            linearLayoutRecords.removeAllViews();
            for (Reminder obj : reminderList) {


                int id = obj.id;
                String reminder = obj.reminder;
                String time = obj.time;
                String date = obj.date;

                String textViewContents = Integer.toString(id) + " | " + reminder + " | " + time + " | " + date;

                TextView textViewReminderItem = new TextView(this);

                textViewReminderItem.setPadding(0, 10, 0, 10);
                textViewReminderItem.setText(textViewContents);
                textViewReminderItem.setTag(Integer.toString(id));
                textViewReminderItem.setTextSize(16);

                linearLayoutRecords.addView(textViewReminderItem);
            }

        } else {
            linearLayoutRecords.removeAllViews();
            TextView locationItem = new TextView(this);
            locationItem.setPadding(8, 8, 8, 8);
            locationItem.setText("No records yet.");

            linearLayoutRecords.addView(locationItem);
        }
    }

    //sort reminder list by date and time decreassing way

    public List<Reminder> sortMyReminderList(List<Reminder> list) {
        Collections.sort(list, new Comparator<Reminder>() {
            @Override
            public int compare(Reminder o1, Reminder o2) {

                long a = o1.getDateTime();
                long b = o2.getDateTime();
                if (a > b)
                    return -1;
                else if (a == b) // it's equals
                    return 0;
                else
                    return 1;
            }
        });
        return list;
    }
}

