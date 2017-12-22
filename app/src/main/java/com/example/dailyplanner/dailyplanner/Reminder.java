package com.example.dailyplanner.dailyplanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by valentin on 11/17/17.
 */

public class Reminder {
    int id;
    String reminder;
    String time;
    String date;

    public Reminder() {

    }

    public long getDateTime() {
        String myDate = date + " " + time;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date fdate = null;
        try {
            fdate = sdf.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = fdate.getTime();
        return millis;
    }
}
