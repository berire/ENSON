package com.example.Mnemonica;

/**
 * Created by handeozyazgan on 01/05/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Bengu on 28.3.2017.
 */

public class depo_createAct extends AppCompatActivity {
    private Button add;
    private Button sendEvt;
    private Button strtAlarm;
    private Button addLesson;
    private Button addExam;
    private EditText actName;
    private EditText dest;
    private TimePicker tp;
    private DatePicker dp;
    String actN;
    String uid;
    static int num = 0;
    static int numberOfActivity=0;
    int hour;
    int minute;
    int day;
    int month;
    int year;
    String dayStr;
    String monthStr;
    String yearStr;
    String hourStr;
    String minuteStr;
    String numOfAct;
    String activityID;
    String destination;
    private EditText attandanceLimitTxt;
    String attandanceLimitS;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    String activityType;
    String limit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_layout);
        Firebase.setAndroidContext(this);

        init();

        final FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
        uid = user2.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();


        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                actN = actName.getText().toString();
                hour = tp.getCurrentHour();
                minute = tp.getCurrentMinute();
                day = dp.getDayOfMonth();
                month = dp.getMonth();
                year = dp.getYear();
                destination = dest.getText().toString();


                if (TextUtils.isEmpty(actN)) {
                    Toast.makeText(getApplicationContext(), "Enter Title of Activity!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Adding name of activity to database
                hourStr = String.valueOf(hour);
                minuteStr = String.valueOf(minute);
                dayStr = String.valueOf(day);
                monthStr = String.valueOf(month);
                yearStr = String.valueOf(year);
                num++;
                activityID = String.valueOf(num);

                DatabaseReference newRef =
                        myRef.child("Users").child(uid).child("activities").push();
                newRef.child("Activity Name").setValue(actN);
                newRef.child("Activity Hour").setValue(hourStr);
                newRef.child("Activity Minute").setValue(minuteStr);
                newRef.child("Activity Day").setValue(dayStr);
                newRef.child("Activity Month").setValue(monthStr);
                newRef.child("Activity Year").setValue(yearStr);
                newRef.child("Activity Destination").setValue(destination);
                numberOfActivity++;
                //numOfAct = String.valueOf(numberOfActivity);
                myRef.child("Users").child(uid).child("Number of Activities").setValue(numberOfActivity);
                //Adding location information of activity to database


            }
        });


        sendEvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(depo_createAct.this,SendEvent.class);
                intent.putExtra("dest", destination);
                intent.putExtra("name", actN);
                intent.putExtra("hour", hour);
                intent.putExtra("minute", minute);
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                intent.putExtra("day", day);
                startActivity(intent);
            }
        });

        strtAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new
                        Intent(depo_createAct.this,AddActToSchedule.class);
                startActivity(intent);
            }
        });

        addLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(depo_createAct.this,activityType + "is selected",Toast.LENGTH_LONG).show();
                actN = actName.getText().toString();
                hour = tp.getCurrentHour();
                minute = tp.getCurrentMinute();
                day = dp.getDayOfMonth();
                month = dp.getMonth();
                year = dp.getYear();
                destination = dest.getText().toString();

                Toast.makeText(depo_createAct.this, "OF", Toast.LENGTH_LONG);

                if (TextUtils.isEmpty(actN)) {
                    Toast.makeText(getApplicationContext(), "Enter Title of Activity!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Adding name of activity to database
                hourStr = String.valueOf(hour);
                minuteStr = String.valueOf(minute);
                dayStr = String.valueOf(day);
                monthStr = String.valueOf(month);
                yearStr = String.valueOf(year);
                num++;
                activityID = String.valueOf(num);

//myRef.child("Users").child(uid).child("activities").child(activityID).child("ActivityName").setValue(eventName);
                DatabaseReference newRef =
                        myRef.child("Users").child(uid).child("activities").push();
                newRef.child("Activity Name").setValue(actN);
                newRef.child("Activity Hour").setValue(hourStr);
                newRef.child("Activity Minute").setValue(minuteStr);
                newRef.child("Activity Day").setValue(dayStr);
                newRef.child("Activity Month").setValue(monthStr);
                newRef.child("Activity Year").setValue(yearStr);
                newRef.child("Activity Destination").setValue(destination);
                numberOfActivity++;
                //numOfAct = String.valueOf(numberOfActivity);
                myRef.child("Users").child(uid).child("Number of Activities").setValue(numberOfActivity);

                DatabaseReference newRef2 =
                        myRef.child("Users").child(uid).child("Lessons").push();
                newRef2.child("Activity Name").setValue(actN);
                newRef2.child("Activity Hour").setValue(hourStr);
                newRef2.child("Activity Minute").setValue(minuteStr);
                newRef2.child("Activity Day").setValue(dayStr);
                newRef2.child("Activity Month").setValue(monthStr);
                newRef2.child("Activity Year").setValue(yearStr);
                newRef2.child("Activity Destination").setValue(destination);
                newRef2.child("Attendance Limit").setValue(limit);
                newRef2.child("Absence").setValue("0");
            }
        });
        addExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actN = actName.getText().toString();
                hour = tp.getCurrentHour();
                minute = tp.getCurrentMinute();
                day = dp.getDayOfMonth();
                month = dp.getMonth();
                month = dp.getMonth();
                year = dp.getYear();
                destination = dest.getText().toString();


                if (TextUtils.isEmpty(actN)) {
                    Toast.makeText(getApplicationContext(), "Enter Title of Activity!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Adding name of activity to database
                hourStr = String.valueOf(hour);
                minuteStr = String.valueOf(minute);
                dayStr = String.valueOf(day);
                monthStr = String.valueOf(month);
                yearStr = String.valueOf(year);
                num++;
                activityID = String.valueOf(num);

//myRef.child("Users").child(uid).child("activities").child(activityID).child("ActivityName").setValue(eventName);
                DatabaseReference newRef =
                        myRef.child("Users").child(uid).child("activities").push();
                newRef.child("Activity Name").setValue(actN);
                newRef.child("Activity Hour").setValue(hourStr);
                newRef.child("Activity Minute").setValue(minuteStr);
                newRef.child("Activity Day").setValue(dayStr);
                newRef.child("Activity Month").setValue(monthStr);
                newRef.child("Activity Year").setValue(yearStr);
                newRef.child("Activity Destination").setValue(destination);
                numberOfActivity++;
                //numOfAct = String.valueOf(numberOfActivity);
                myRef.child("Users").child(uid).child("Number of Activities").setValue(numberOfActivity);

                DatabaseReference newRef2 =
                        myRef.child("Users").child(uid).child("Exams").push();
                newRef2.child("Activity Name").setValue(actN);
                newRef2.child("Activity Hour").setValue(hourStr);
                newRef2.child("Activity Minute").setValue(minuteStr);
                newRef2.child("Activity Day").setValue(dayStr);
                newRef2.child("Activity Month").setValue(monthStr);
                newRef2.child("Activity Year").setValue(yearStr);
                newRef2.child("Activity Destination").setValue(destination);
            }
        });

    }

    private void init(){
        add = (Button) findViewById(R.id.add);
        sendEvt = (Button) findViewById(R.id.sendEvent);
        strtAlarm = (Button) findViewById(R.id.strtAlarm);
        actName = (EditText) findViewById(R.id.actName);
        dp = (DatePicker)findViewById(R.id.eventDate);
        tp = (TimePicker)findViewById(R.id.eventTime);
        dest = (EditText) findViewById(R.id.dest);
        addLesson = (Button) findViewById(R.id.attandanceCheck);
        addExam = (Button) findViewById(R.id.preExamWarning);
        attandanceLimitS = attandanceLimitTxt.getText().toString();
        limit = "10";

        spinner = (Spinner) findViewById(R.id.spinnerActType);
        adapter = ArrayAdapter.createFromResource(this, R.array.activity_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getBaseContext(), activityType, Toast.LENGTH_SHORT);
                activityType = spinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
