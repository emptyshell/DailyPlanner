package com.example.dailyplanner.dailyplanner;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

/**
 * Created by valentin on 11/16/17.
 */

public class UpdateActivity extends AppCompatActivity {
    Reminder rem = new Reminder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_layout);
        //MainActivity.initAHBottomNavigation ();

        initBottomNav();
        setInitAHBottomNavigationListener();
        final ReminderDB db = new ReminderDB(this);
        Button searchButton = (Button) findViewById(R.id.searchButton);
        final EditText editTextID = (EditText) findViewById(R.id.editTextID);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextID.getText().toString().isEmpty()) {
                    editTextID.setError(getResources().getString(R.string.errorID));
                    return;
                } else {
                    rem = db.readSingleRecord(Integer.parseInt(editTextID.getText().toString()));

                }
            }
        });
        getOldData();
        Button button = (Button) findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setNewData(rem)) {
                    Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(UpdateActivity.this, getResources().getString(R.string.deletefailed), 5000);
                }
            }
        });


    }

    public boolean setNewData(Reminder reminder) {

        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        final EditText editText = (EditText) findViewById(R.id.editText);
        final EditText idEditText = (EditText) findViewById(R.id.editTextID);

        reminder = new Reminder();
        // geting data and saving it into a string
        reminder.date = Integer.toString(datePicker.getDayOfMonth()) + "." + Integer.toString(datePicker.getMonth() + 1) + "." + Integer.toString(datePicker.getYear());
        Log.d("DATE", reminder.date);
        reminder.time = Integer.toString(timePicker.getHour()) + ":" + Integer.toString(timePicker.getMinute());
        //Log.d("TIME", time);
        reminder.reminder = editText.getText().toString();
        //Log.d("REMINDER", reminder);
        reminder.id = Integer.parseInt(idEditText.getText().toString());

        ReminderDB db = new ReminderDB(this);
        boolean check = db.update(reminder);
        return check;

    }

    public void getOldData() {

        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        final EditText editText = (EditText) findViewById(R.id.editText);
        final EditText idEditText = (EditText) findViewById(R.id.editTextID);
        Button searchButton = (Button) findViewById(R.id.searchButton);
        final Reminder[] reminder = {new Reminder()};

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = -1;

                ReminderDB db = new ReminderDB(UpdateActivity.this);
                if (idEditText.getText().toString().isEmpty()) {
                    idEditText.setError(getResources().getString(R.string.errorID));
                    return;
                } else {
                    id = Integer.parseInt(idEditText.getText().toString());
                }
                int month;
                int year;
                reminder[0] = db.readSingleRecord(id);
                editText.setText(reminder[0].reminder);
                int hour = Integer.parseInt(reminder[0].time.substring(0, reminder[0].time.indexOf(":")));
                //Log.d("HOUR", Integer.toString(hour));
                //Log.d("CURRENT_STR", reminder.time);
                int minute = Integer.parseInt(reminder[0].time.substring(reminder[0].time.indexOf(":") + 1, reminder[0].time.length()));
                //Log.d("MINUTE", Integer.toString(minute));
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
                int day = Integer.parseInt(reminder[0].date.substring(0, reminder[0].date.indexOf(".")));
                Log.d("DAY", Integer.toString(day));
                String processed = reminder[0].date.replace(Integer.toString(day) + ".", "");
                Log.d("PROCESSED", processed);
                if (processed.length() == 4) {
                    month = day;
                    year = Integer.parseInt(processed);
                } else {
                    reminder[0].date.replace(reminder[0].date, processed);
                    Log.d("PROCESSED", reminder[0].date);
                    month = Integer.parseInt(processed.substring(0, processed.indexOf(".")));
                    //Log.d("MONTH", Integer.toString(month));
                    processed = reminder[0].date.replace(Integer.toString(day) + "." + Integer.toString(month) + ".", "");
                    year = Integer.parseInt(processed);
                    //Log.d("YEAR", Integer.toString(year));
                }


                datePicker.updateDate(year, month - 1, day);
                db.close();
            }
        });

    }

    public void initBottomNav() {
        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem date = new AHBottomNavigationItem(R.string.date, R.drawable.ic_date, R.color.addButtonColor);
        AHBottomNavigationItem time = new AHBottomNavigationItem(R.string.time, R.drawable.ic_time, R.color.updateButtonColor);

        bottomNavigation.addItem(date);
        bottomNavigation.addItem(time);

        bottomNavigation.setColored(true);
    }

    public void setInitAHBottomNavigationListener() {

        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        timePicker.setVisibility(View.INVISIBLE);
                        datePicker.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        timePicker.setVisibility(View.VISIBLE);
                        datePicker.setVisibility(View.INVISIBLE);
                        break;
                }
                return true;
            }
        });

    }
}



