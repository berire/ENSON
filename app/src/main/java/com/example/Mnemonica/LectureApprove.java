package com.example.Mnemonica;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.toIntExact;

/**
 * Created by user on 8.5.2017.
 */

public class LectureApprove extends AppCompatActivity {
    public static ArrayList<Word> lectures=new ArrayList<>();
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseUser user2;
    private String uid;
    private static int numberOfActivity=0;
    private DatabaseReference mPostReference;
    private ArrayList<String> Actids=new ArrayList<>();
    private String anid;
    private String value;
    private static ArrayList<Boolean> verfy=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.lect_approve);

        int rb_numb=lectures.size();
        user2 = FirebaseAuth.getInstance().getCurrentUser();
        uid = user2.getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        LinearLayout my_layout = (LinearLayout)findViewById(R.id.layout_lect);
        ArrayList<CheckBox> cb=new ArrayList<>();



        mPostReference = FirebaseDatabase.getInstance().getReference( )
                .child("Users").child(uid);
        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Number of Activities").getValue()!=null)
                {
                long dmb=0;
                dmb=(long)dataSnapshot.child("Number of Activities").getValue();
                    if(dmb!=0)
                    {
                        numberOfActivity=(int)dmb;
                    }
                    else
                    {numberOfActivity=0;}
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // [START_EXCLUDE]
                Toast.makeText(LectureApprove.this, "Failed to load activities",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });




        for (int i = 0; i < rb_numb; i++)
        {
            String ap=" Name: " + lectures.get(i).getcontext()+" Hour: "+ lectures.get(i).gethour()+"40"+ " Day: "+lectures.get(i).getRD();
            TableRow row =new TableRow(this);
            row.setId(i);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(i);
            checkBox.setText(ap);
            row.addView(checkBox);
            my_layout.addView(row);
            cb.add(checkBox);
            verfy.add(false);
        }



        for (int i = 0; i < rb_numb; i++)
        {

            CheckBox checkBox =cb.get(i);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked==true)
                    {
                        Toast.makeText(LectureApprove.this, "buttonView.getId(): "+ buttonView.getId(),
                                Toast.LENGTH_SHORT).show();

                        verfy.set(buttonView.getId(),true);

                    }
                }
            });
        }

        Button next=(Button) findViewById(R.id.buttn_lect);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i=0;i<verfy.size();i++)
                {
                    String lesName="";
                    int hour1=0;
                    int minute1=0;
                    int day1=0;
                    int monthStr1=0;
                    int yearStr1=0;
                    String RD="";
                    String destination1="";
                    if(verfy.get(i)==true)
                    {
                        lesName=lectures.get(i).getcontext();
                        hour1=lectures.get(i).gethour();
                        minute1=40;
                        day1=lectures.get(i).getDay();
                        monthStr1=4;
                        yearStr1=2017;
                        destination1="Bilkent Universitesi";

                        String actN = lesName;
                        int hour = hour1;
                        int minute = minute1;
                        String dayStr = String.valueOf(day1);
                        String monthStr =String.valueOf( monthStr1);
                        String yearStr = String.valueOf(yearStr1);
                        String destination =destination1;


                        if (TextUtils.isEmpty(actN)) {
                            Toast.makeText(getApplicationContext(), "Enter Title of Activity!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //Adding name of activity to database
                        String hourStr = String.valueOf(hour);
                        String minuteStr = String.valueOf(minute);
                        //myRef.child("Users").child(uid).child("activities").child(activityID).child("Activity Name").setValue(eventName);
                        DatabaseReference newRef =  myRef.child("Users").child(uid).child("activities").push();
                        newRef.child("Activity Name").setValue(actN);
                        newRef.child("Activity Hour").setValue(hourStr);
                        newRef.child("Activity Minute").setValue(minuteStr);
                        newRef.child("Activity Day").setValue(dayStr);
                        newRef.child("Activity Month").setValue(monthStr);
                        newRef.child("Activity Year").setValue(yearStr);
                        newRef.child("Activity Destination").setValue(destination);
                        newRef.child("Absence").setValue("0");
                        numberOfActivity++;
                        //numOfAct = String.valueOf(numberOfActivity);
                        myRef.child("Users").child(uid).child("Number of Activities").setValue(numberOfActivity);
                        //Adding location information of activity to database

                        Toast.makeText(getApplicationContext(), "Classess Added!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LectureApprove.this,Menu.class);
                        startActivity(intent);
                    }
                }





            }});


    }


}
