package com.example.Mnemonica;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import butterknife.Bind;
import butterknife.ButterKnife;

public class Schedule extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {


    ArrayList<Act> actions;
    ArrayList<String> name;
    private FirebaseAuth mAuth;
    private DatabaseReference dataRef;
    private FirebaseDatabase myRef;
    private String userID;
    private DatabaseReference mPostReference;
    ArrayList<String> Actids=new ArrayList<>();
    String anid;
    String value;

        private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

        @Bind(R.id.calendarView)
        MaterialCalendarView widget;

        @Bind(R.id.textView)
        TextView textView;


    public Schedule() {}

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.schedule);
            ButterKnife.bind(this);


            //Previous versions of Firebase
            Firebase.setAndroidContext(this);

            actions=new ArrayList<>();

            mAuth = FirebaseAuth.getInstance();
            final FirebaseUser user = mAuth.getCurrentUser();
            userID = user.getUid();

            myRef =FirebaseDatabase.getInstance();
            dataRef = myRef.getReference("Users");

            // Initialize Database
            mPostReference = FirebaseDatabase.getInstance().getReference( )
                  .child("Users").child(userID).child("activities");

            widget.setOnDateChangedListener(this);
            widget.setOnMonthChangedListener(this);

            //Setup initial text
            textView.setText(getSelectedDatesString());
        }
    @Override
    public void onStart() {
        super.onStart();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()){
                    anid=(String)child.getKey();
                    Actids.add(anid);
                }
                for (int i=0;i<Actids.size();i++)
                {
                    Act act=new Act();
                    String aKey= Actids.get(i);
                    value=(String) dataSnapshot.child(aKey).child("Activity Name").getValue();
                    act.setActName(value);
                    act.setDate(Integer.parseInt(((String) dataSnapshot.child(aKey).child("Activity Day").getValue()).toString()));
                    act.setMonth(Integer.parseInt(( (String) dataSnapshot.child(aKey).child("Activity Month").getValue()).toString()));
                    act.setDestination((((String) dataSnapshot.child(aKey).child("Activity Destination").getValue()).toString()));
                    act.setHour(Integer.parseInt(((String) dataSnapshot.child(aKey).child("Activity Hour").getValue()).toString()));
                    act.setYear(Integer.parseInt(((String) dataSnapshot.child(aKey).child("Activity Year").getValue()).toString()));
                    act.setMinute(Integer.parseInt(((String) dataSnapshot.child(aKey).child("Activity Minute").getValue()).toString()));
                    actions.add(act);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // [START_EXCLUDE]
                Toast.makeText(Schedule.this, "Failed to load activities",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mPostReference.addValueEventListener(postListener);

        Set<Act> hs = new HashSet<>();
        hs.addAll(actions);
        actions.clear();
        actions.addAll(hs);

    }


        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
            ArrayList<Integer> inx=new ArrayList<>();
            CalendarDay mate = widget.getSelectedDate();
            textView.setText(getSelectedDatesString());

            inx=getActivityIndex(mate);
            String out="";
            if(inx.size()>=1) {
                for(int g=0;g<inx.size();g++)
                    out=out +" EVENT NAME: " + actions.get(inx.get(g)).getActName() + " EVENT PLACE: " + actions.get(inx.get(g)).getDestination() + " EVENT HOUR: " + actions.get(inx.get(g)).getHour();

                textView.setText(out);
            }
            else
                Toast.makeText(Schedule.this, "NO Activity",
                        Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
            //noinspection ConstantConditions
            //getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
            textView.setText("MONTH CHANGED");
        }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }

    private ArrayList<Integer> getActivityIndex(CalendarDay Date)
    {
        int year,month,day;
        ArrayList<Integer> index=new ArrayList<>();
        CalendarDay cd;

        for (int i=0; i<actions.size(); i++)
        {
            day=actions.get(i).getDate();
            year=actions.get(i).getYear();
            month=actions.get(i).getMonth();
            cd=new CalendarDay(year,month,day);

            String as=Date.toString();
            String bs=cd.toString();

            if((as.toLowerCase()).equals(bs.toLowerCase()))
            {
                index.add(i);
            }
        }

        return index;

    }

}



