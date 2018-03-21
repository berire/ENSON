package com.example.Mnemonica;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Mnemonica.TimeBetweenLocations.MapsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.onesignal.OneSignal;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by Bengu on 16.3.2017.
 */


public class Menu extends Activity {
    Button dataTest;
    Button scheduleBtn;
    Button editAccount;
    Button alarm;
    Button showAct;
    Button addAct;
    Button btnlocation, findAddressLocation, findDistTime, tryButton, pushNotification,schedule;
    Button addFriend;
    Button showFriends;
    Button viewAw;
    private FirebaseAuth mAuth;
    static String LoggedIn_User_Email;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRef2;
    String uid;
    static String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
        uid = user2.getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        Intent intent = this.getIntent();
        String userName = intent.getStringExtra("userName");
        myRef.child("Users").child(uid).child("Name").setValue(userName);

        myRef.child("Users").child(uid).child("Mail").setValue(user2.getEmail());


        dataTest=(Button)findViewById(R.id.dataTest);
        scheduleBtn = (Button)findViewById(R.id.schedule);
        editAccount = (Button)findViewById(R.id.editAccount);
        showAct = (Button) findViewById(R.id.showActivity);
        addAct = (Button) findViewById(R.id.addAct);
        viewAw = (Button) findViewById(R.id.viewAw);


        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Menu.this,MainActivity.class);
                startActivity(intent);
            }
        });

        showAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Menu.this,ViewActivities.class);
                startActivity(intent);
            }
        });

        addAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Menu.this,CreateAct.class);
                startActivity(intent);
            }
        });

        showFriends=(Button)findViewById(R.id.addFriend) ;
        showFriends.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Hotel.class);
                startActivity(intent);
            }
        });

        viewAw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, ViewAwards.class);
                startActivity(intent);
            }
        });

        schedule= (Button) findViewById(R.id.schedule);
        schedule.setVisibility(View.VISIBLE);
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Menu.this,Schedule.class);
                startActivity(intent);

            }
        });
        dataTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this,SimpleAndroidOCRActivity.class);

                //Intent intent = new Intent(Menu.this,Schedule.class);
                startActivity(intent);
            }
        });


    }

    private void Init() {

        dataTest=(Button)findViewById(R.id.dataTest);
        scheduleBtn = (Button)findViewById(R.id.schedule);
        editAccount = (Button)findViewById(R.id.editAccount);
        showAct = (Button) findViewById(R.id.showActivity);
        addAct = (Button) findViewById(R.id.addAct);
        viewAw = (Button) findViewById(R.id.viewAw);
    }
}






