package com.example.Mnemonica;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;
/**
 * Created by Bengu on 18.4.2017.
 */

public class CreateEvent extends Activity {

    private EditText eventName;
    private EditText eventPlace;
    private Button sendEvent;
    private Button addEvent;
    private TimePicker eventTİme;
    private DatePicker eventDate;
    private String eName;
    private String place;
    static int  num = 0;
    static int numberOfActivity=0;
    String dayStr;
    String monthStr;
    String yearStr;
    String hourStr;
    String minuteStr;
    String value;
    private String userID;
    private String activityID;
    String uid;
    ArrayList<String> fList;
    String uMail;

    private FirebaseAuth mAuth;
    static String LoggedIn_User_Email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);
        OneSignal.startInit(this).init();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        LoggedIn_User_Email =user.getEmail();
        // LoggedIn_User_Email = "handeozyazgan@gmail.com";
        OneSignal.sendTag("User_ID", LoggedIn_User_Email);
        uid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        eventName = (EditText) findViewById(R.id.eventName);
        eventPlace = (EditText) findViewById(R.id.eventPlace);
        sendEvent = (Button) findViewById(R.id.sendEvent);
        addEvent = (Button) findViewById(R.id.addEvent);
        eventTİme = (TimePicker) findViewById(R.id.eventTime);
        eventDate = (DatePicker) findViewById(R.id.eventDate);

        fList = new ArrayList<>();

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eName = eventName.getText().toString();
                hourStr = String.valueOf(eventTİme.getCurrentHour());
                minuteStr = String.valueOf(eventTİme.getCurrentMinute());
                dayStr = String.valueOf(eventDate.getDayOfMonth());
                monthStr = String.valueOf(eventDate.getMonth());
                yearStr = String.valueOf(eventDate.getYear());

                if (TextUtils.isEmpty(eName)) {
                    Toast.makeText(getApplicationContext(), "Enter Title of Activity!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Adding name of activity to database
                num++;
                activityID = String.valueOf(num);
                //myRef.child("Users").child(uid).child("activities").child(activityID).child("Activity Name").setValue(eventName);
                DatabaseReference newRef =  myRef.child("Users").child(uid).child("activities").child(activityID);
                newRef.child("Activity Name").setValue(eName);
                newRef.child("Activity Hour").setValue(hourStr);
                newRef.child("Avtivity Minute").setValue(minuteStr);
                newRef.child("Avtivity Day").setValue(dayStr);
                newRef.child("Avtivity Month").setValue(monthStr);
                newRef.child("Avtivity Year").setValue(yearStr);
                numberOfActivity++;
                //numOfAct = String.valueOf(numberOfActivity);
                myRef.child("Users").child(uid).child("Number of Activities").setValue(numberOfActivity);
                //Adding location information of activity to database

            }
        });

        sendEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEvent.this, SendEvent.class);
                intent.putExtra("name",eName);
                startActivity(intent);
                //sendNotification();
            }
        });

       /* DatabaseReference fRef = myRef.child("Users").child(uid).child("Friends");
        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    value = dataSnapshot.getValue(String.class);
                    fList.add(value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference mailRef = myRef.child("Users").child(uid).child("Mail");
        mailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uMail = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });*/
    }
    private void sendNotification()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email = "";

                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    if (Menu.LoggedIn_User_Email.equals(uMail)) {
                        for (int i=0; i<fList.size(); i++){
                            send_email = fList.get(i);
                        }
                        // send_email = "bengu.akbostanci06@gmail.com";
                    } else {
                        send_email = uMail;
                    }

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic NGJjYWQ3NjYtYTllZS00M2I0LWEyNzAtNzliNmZlZTA3MWJh");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"3e0e8f7c-360d-4966-81a8-f225735cf025\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"English Message\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }
}
