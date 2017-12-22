package com.example.dailyplanner.dailyplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by valentin on 11/17/17.
 */

public class ReminderDB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "reminderDB";
    private static final String TABLE_NAME = "reminder";
    //table content
    private static final String KEY_ID = "id";
    private static final String REMINDER_KEY = "reminder";
    private static final String TIME_KEY = "time";
    private static final String DATE_KEY = "date";

    public ReminderDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + REMINDER_KEY + " TEXT, " + TIME_KEY + " TEXT, " + DATE_KEY + " TEXT) ";
        //String createVirtualTable = "CREATE VIRTUAL TABLE "+TABLE_NAME + " USING fts4 (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+REMINDER_KEY+" TEXT, "+TIME_KEY+" TEXT, "+DATE_KEY+" TEXT) ";
        db.execSQL(createTable);
        //db.execSQL(createVirtualTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(Reminder reminder) {
        boolean createSuccesful = false;

        ContentValues values = new ContentValues();

        values.put(REMINDER_KEY, reminder.reminder);
        values.put(TIME_KEY, reminder.time);
        values.put(DATE_KEY, reminder.date);

        SQLiteDatabase db = this.getWritableDatabase();

        createSuccesful = db.insert(TABLE_NAME, null, values) > 0;
        db.close();

        return createSuccesful;
    }

    public int countDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM reminder";
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();

        return recordCount;
    }

    public List<Reminder> read() {
        List<Reminder> recordList = new ArrayList<Reminder>();

        String sql = "SELECT * FROM reminder ORDER BY id ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                String reminder = cursor.getString(cursor.getColumnIndex(REMINDER_KEY));
                String time = cursor.getString(cursor.getColumnIndex(TIME_KEY));
                String date = cursor.getString(cursor.getColumnIndex(DATE_KEY));

                Reminder reminderObj = new Reminder();
                reminderObj.id = id;
                reminderObj.reminder = reminder;
                reminderObj.time = time;
                reminderObj.date = date;

                recordList.add(reminderObj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordList;
    }

    public Reminder readSingleRecord(int reminderID) {
        Reminder reminderObj = null;
        String sql = "SELECT * FROM reminder WHERE id=" + Integer.toString(reminderID);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID)));
            String reminder = cursor.getString(cursor.getColumnIndex(REMINDER_KEY));
            String time = cursor.getString(cursor.getColumnIndex(TIME_KEY));
            String date = cursor.getString(cursor.getColumnIndex(DATE_KEY));

            reminderObj = new Reminder();
            reminderObj.id = id;
            reminderObj.reminder = reminder;
            reminderObj.time = time;
            reminderObj.date = date;
        }
        cursor.close();
        db.close();
        return reminderObj;
    }

    public boolean update(Reminder reminder) {
        ContentValues values = new ContentValues();

        values.put(REMINDER_KEY, reminder.reminder);
        values.put(TIME_KEY, reminder.time);
        values.put(DATE_KEY, reminder.date);
        String strFilter = "id=" + Integer.toString(reminder.id);

        SQLiteDatabase db = this.getWritableDatabase();

        boolean state = db.update(TABLE_NAME, values, strFilter, null) > 0;
        return state;
    }

    public boolean delete(int id) {
        boolean deleteSuccesful = false;

        SQLiteDatabase db = this.getWritableDatabase();
        deleteSuccesful = db.delete(TABLE_NAME, "id= '" + id + "'", null) > 0;
        db.close();
        return deleteSuccesful;
    }

    public List<Reminder> find(String string) {
        List<Reminder> recordList = new ArrayList<Reminder>();

        Reminder reminderObj = null;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + REMINDER_KEY + " LIKE '%" + string + "%' OR " + KEY_ID + " LIKE '%" + string + "%' OR " + DATE_KEY + " LIKE '%" + string + "%' OR " + TIME_KEY + " LIKE '%" + string + "%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                String reminder = cursor.getString(cursor.getColumnIndex(REMINDER_KEY));
                String time = cursor.getString(cursor.getColumnIndex(TIME_KEY));
                String date = cursor.getString(cursor.getColumnIndex(DATE_KEY));

                reminderObj = new Reminder();
                reminderObj.id = id;
                reminderObj.reminder = reminder;
                reminderObj.time = time;
                reminderObj.date = date;
                Log.d("FOUND", Integer.toString(id));


                recordList.add(reminderObj);
            } while (cursor.moveToNext());


        }
        cursor.close();
        db.close();
        return recordList;
    }
}
