package com.example.Mnemonica;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Bengu on 28.3.2017.
 */

public class CreateAct extends AppCompatActivity {
    private Button add;
    private Button showAct;
    private EditText actName;
    private EditText dest;
    private TimePicker tp;
    private DatePicker dp;
    String actN;
    String uid;
    static int  num = 0;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_layout);

        init();

        final FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
        uid = user2.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        //num = numberOfActivity;


        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                actN = actName.getText().toString();
                hour = tp.getCurrentHour();
                minute = tp.getCurrentMinute();
                dayStr = String.valueOf(dp.getDayOfMonth());
                monthStr = String.valueOf(dp.getMonth());
                yearStr = String.valueOf(dp.getYear());
                destination = dest.getText().toString();


                if (TextUtils.isEmpty(actN)) {
                    Toast.makeText(getApplicationContext(), "Enter Title of Activity!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Adding name of activity to database
                hourStr = String.valueOf(hour);
                minuteStr = String.valueOf(minute);
                num++;
                activityID = String.valueOf(num);
                //myRef.child("Users").child(uid).child("activities").child(activityID).child("Activity Name").setValue(eventName);
                DatabaseReference newRef =  myRef.child("Users").child(uid).child("activities").push();
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


       /* showAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAct.this,ActList.class);
                startActivity(intent);
            }
        });*/


    }

    private void init(){
        add = (Button) findViewById(R.id.add);
        //showAct = (Button) findViewById(R.id.showAct);
        actName = (EditText) findViewById(R.id.actName);
        dp = (DatePicker)findViewById(R.id.eventDate);
        tp = (TimePicker)findViewById(R.id.eventTime);
        dest = (EditText) findViewById(R.id.dest);
    }
}