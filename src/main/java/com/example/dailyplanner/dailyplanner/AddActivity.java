package com.example.dailyplanner.dailyplanner;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class AddActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        initBottomNav();
        setInitAHBottomNavigationListener();


        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getData()) {
                    String msg = getResources().getString(R.string.addSucces);
                    Toast.makeText(AddActivity.this, msg, 5000).show();
                } else {
                    String msg = getResources().getString(R.string.deletefailed);
                    Toast.makeText(AddActivity.this, msg, 5000).show();
                    return;
                }
                Intent intent = new Intent(AddActivity.this, MainActivity.class);
                startActivity(intent);
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

    public boolean getData() {

        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        final EditText editText = (EditText) findViewById(R.id.editText);
        Reminder reminder = new Reminder();
        // geting data and saving it into a string
        reminder.date = Integer.toString(datePicker.getDayOfMonth()) + "." + Integer.toString(datePicker.getMonth() + 1) + "." + Integer.toString(datePicker.getYear());
        //Log.d("DATE", date);
        reminder.time = Integer.toString(timePicker.getHour()) + ":" + Integer.toString(timePicker.getMinute());
        //Log.d("TIME", time);
        if (editText.getText().toString().isEmpty()) {
            editText.setError(getResources().getString(R.string.errorReminder));
            return false;
        } else {
            reminder.reminder = editText.getText().toString();
        }
        //Log.d("REMINDER", reminder);


        ReminderDB db = new ReminderDB(this);
        boolean status = db.addData(reminder);
        db.close();
        return status;
    }

}



