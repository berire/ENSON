package com.example.Mnemonica;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by Bengu on 6.4.2017.
 */


public class AddActToSchedule extends AppCompatActivity {


    private FirebaseAuth mAuth;


    private DatabaseReference dataRef;

    private FirebaseDatabase myRef;
    private String userID;

    GPSTracker gps;
    String key="";
    String key2="";
    ArrayAdapter<String> arrAdap;
    ArrayList<Integer> h;
    ArrayList<Integer> min;
    ArrayList<Integer> d;
    ArrayList<Integer> m;
    ArrayList<Integer> y;
    ArrayList<String> name;
    ArrayList<String> sss;
    ArrayList<String> keys;
    ArrayList<Integer> h2;
    ArrayList<Integer> min2;
    ArrayList<Integer> d2;
    ArrayList<Integer> m2;
    ArrayList<Integer> y2;
    ArrayList<String> name2;
    ArrayList<Act> actList;
    ArrayList<Act> actList2;
    ArrayList<Act> actList3;
    ArrayList<Act> actList4;
    ArrayList<Act> actList5;

    int minute;
    int hour;
    int date;
    int month;
    int index3;
    int index4;
    int index5;
    int index6;
    boolean check =true;
    String value;
    int num=0;
    int no;
    int newNum;
    int a;
    int b;
    Button alrmTry;
    double latitudeFirst = 0;
    double longitudeFirst = 0;
    double latitudeSecond;
    double longitudeSecond;
    private Handler handler;



    public String destination;


    private ListView mListView;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.addschedule);
        //alrmTry = (Button)findViewById(R.id.alrmTry);



        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef =FirebaseDatabase.getInstance();
        dataRef = myRef.getReference("Users");

        h = new ArrayList<>();
        min = new ArrayList<>();
        d = new ArrayList<>();
        m = new ArrayList<>();
        y = new ArrayList<>();
        name = new ArrayList<>();
        sss = new ArrayList<>();
        keys = new ArrayList<>();
        h2 = new ArrayList<>();
        min2 = new ArrayList<>();
        d2 = new ArrayList<>();
        m2 = new ArrayList<>();
        y2 = new ArrayList<>();
        name2 = new ArrayList<>();
        actList = new ArrayList<>();
        actList2 = new ArrayList<>();
        actList3 = new ArrayList<>();
        actList4 = new ArrayList<>();
        actList5 = new ArrayList<>();

      /*  dataRef.child(userID).child("Number of Activities").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                no = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        dataRef.child(userID).child("activities").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    value = child.getValue(String.class);
                    key = dataSnapshot.getKey();
                    if (!(key.equals(key2))){
                        keys.add(key);
                        key2=key;
                    }
                    sss.add(value);
                 /*   if(key2.equals("Activity Hour")){
                        a = Integer.valueOf(value);
                        h.add(a);
                    }
                    else if(key2.equals("Activity Minute")){
                        b = Integer.valueOf(value);
                        m.add(b);
                    }*/

                }
                sendGps();

                // Toast.makeText(AddActToSchedule.this,"2",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Toast.makeText(AddActToSchedule.this,"1",Toast.LENGTH_LONG).show();
        /*alrmTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //al();
            }
        });*/

    }
    private void sendGps(){

        for (int j=0; j<sss.size(); j=j+7){
            Act act = new Act();
            act.setHour(Integer.valueOf(sss.get(j+2)));
            act.setActName(sss.get(j+5));
            act.setDate(Integer.valueOf(sss.get(j)));
            act.setMinute(Integer.valueOf(sss.get(j+3)));
            act.setMonth(Integer.valueOf(sss.get(j+4)));
            act.setYear(Integer.valueOf(sss.get(j+6)));
            act.setDestination(sss.get(j+1));

            act.setKey(keys.get(num));
            actList.add(act);
            num++;
        }

        Collections.sort(actList,Act.ActMinuteComp);
        Collections.sort(actList,Act.ActHourComp);
        Collections.sort(actList,Act.ActDateComp);
        Collections.sort(actList,Act.ActMonthComp);


        //     AlarmManager[] alarmManager = new AlarmManager[actList.size()];
  /*      ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
        for (int i = 0; i < actList.size(); i++) {
            Intent intent = new Intent(AddActToSchedule.this,GPSReceiver.class);
            intent.putExtra("dest", actList.get(i).getDestination());
            intent.putExtra("name", actList.get(i).getActName());
            intent.putExtra("hour", actList.get(i).getHour());
            intent.putExtra("minute", actList.get(i).getMinute());
            intent.putExtra("month", actList.get(i).getMonth());
            intent.putExtra("year", actList.get(i).getYear());
            intent.putExtra("key", actList.get(i).getKey());


            PendingIntent pi = PendingIntent.getBroadcast(
                    AddActToSchedule.this, i, intent, 0);

      //      alarmManager[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
      //      alarmManager[i].set(AlarmManager.RTC_WAKEUP, 0, pi);

            intentArray.add(pi);

        }*/

        //destination = sss.get(1);
        for (int i=0; i<actList.size(); i++){
            // Toast.makeText(AddActToSchedule.this, destination,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(AddActToSchedule.this, GPSReceiver.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("dest", actList.get(i).getDestination());
            intent.putExtra("name", actList.get(i).getActName());
            intent.putExtra("hour", actList.get(i).getHour());
            intent.putExtra("minute", actList.get(i).getMinute());
            intent.putExtra("month", actList.get(i).getMonth());
            intent.putExtra("year", actList.get(i).getYear());
            intent.putExtra("day", actList.get(i).getDate());
            intent.putExtra("key", actList.get(i).getKey());
            intent.putExtra("size", actList.size());
            //intent.putExtra("actlist", actList);
            //intent.putExtra("list", sss);
            //startActivity(intent);
            sendBroadcast(intent);
        }
    }
    private void al(){

        /*for (int k=0; k<sss.size(); k=k+6){
            h.add(Integer.valueOf(sss.get(k)));
            name.add(sss.get(k+1));
            d.add(Integer.valueOf(sss.get(k+2)));
            min.add(Integer.valueOf(sss.get(k+3)));
            m.add(Integer.valueOf(sss.get(k+4)));
            y.add(Integer.valueOf(sss.get(k+5)));
        }*/
        for (int j=0; j<sss.size(); j=j+7){
            Act act = new Act();
            act.setHour(Integer.valueOf(sss.get(j+2)));
            act.setActName(sss.get(j+5));
            act.setDate(Integer.valueOf(sss.get(j)));
            act.setMinute(Integer.valueOf(sss.get(j+3)));
            act.setMonth(Integer.valueOf(sss.get(j+4)));
            act.setYear(Integer.valueOf(sss.get(j+6)));
            act.setDestination(sss.get(j+1));
            act.setKey(keys.get(num));
            actList.add(act);
            num++;
        }


        Collections.sort(actList,Act.ActMinuteComp);
        Collections.sort(actList,Act.ActHourComp);
        Collections.sort(actList,Act.ActDateComp);
        Collections.sort(actList,Act.ActMonthComp);
        //Calendar calendar[] = new Calendar[3];

        Calendar cal[] = new Calendar[actList.size()];


        for (int i = 0; i < actList.size(); i++) {

            cal[i] = Calendar.getInstance();
            cal[i].set(Calendar.HOUR_OF_DAY, actList.get(i).getHour());
            cal[i].set(Calendar.MINUTE, actList.get(i).getMinute());
            cal[i].set(Calendar.SECOND, 0);
            cal[i].set(Calendar.DAY_OF_MONTH, actList.get(i).getDate());
            cal[i].set(Calendar.MONTH, actList.get(i).getMonth());
            cal[i].set(Calendar.YEAR, actList.get(i).getYear());
        }
        int actNum =keys.size();
        AlarmManager[] alarmManager = new AlarmManager[actList.size()];
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
        for (int f = 0; f < cal.length; f++) {
            actNum=actNum-1;
            Intent intent = new Intent(AddActToSchedule.this,
                    AlarmReceiver.class);
            intent.putExtra("str",actList.get(f).getActName());
            intent.putExtra("key",actList.get(f).getKey());
            intent.putExtra("len", actNum+"");

            PendingIntent pi = PendingIntent.getBroadcast(
                    AddActToSchedule.this, f, intent, 0);

            alarmManager[f] = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager[f].set(AlarmManager.RTC_WAKEUP,
                    cal[f].getTimeInMillis(), pi);

            intentArray.add(pi);

        }
    }
}