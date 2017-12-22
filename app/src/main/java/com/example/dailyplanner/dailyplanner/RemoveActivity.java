package com.example.dailyplanner.dailyplanner;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by valentin on 11/16/17.
 */

public class RemoveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_layout);

        readRecords();
        final EditText editText = (EditText) findViewById(R.id.editTextIDtoDelete);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty()) {
                    editText.setError(getResources().getString(R.string.errorDelete));
                    return;
                } else {
                    if (deleteRecord(Integer.parseInt(editText.getText().toString()))) {
                        String msg = getResources().getString(R.string.deleteSucces);
                        Toast.makeText(RemoveActivity.this, msg, 5000).show();
                    } else {
                        String msg = getResources().getString(R.string.deletefailed);
                        Toast.makeText(RemoveActivity.this, msg, 5000).show();
                    }
                }
            }
        });
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RemoveActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void readRecords() {

        LinearLayout linearLayoutRecords = (LinearLayout) findViewById(R.id.linearLayoutRecords);
        linearLayoutRecords.removeAllViews();

        List<Reminder> reminders = new ReminderDB(this).read();

        if (reminders.size() > 0) {

            for (Reminder obj : reminders) {

                int id = obj.id;
                String reminder = obj.reminder;
                String time = obj.time;
                String date = obj.date;

                String textViewContents = Integer.toString(id) + " | " + reminder + " | " + time + " | " + date;

                TextView textViewStudentItem = new TextView(this);
                textViewStudentItem.setPadding(0, 10, 0, 10);
                textViewStudentItem.setText(textViewContents);
                textViewStudentItem.setTag(Integer.toString(id));

                linearLayoutRecords.addView(textViewStudentItem);
            }

        }
    }

    public boolean deleteRecord(int id) {

        ReminderDB db = new ReminderDB(this);
        boolean status = db.delete(id);
        db.close();

        return status;
    }

}
