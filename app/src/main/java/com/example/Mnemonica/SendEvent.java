package com.example.Mnemonica;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.Mnemonica.TimeBetweenLocations.MapsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
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

public class SendEvent extends Activity {
    Button scheduleBtn;
    Button editAccount;
    Button alarm;
    Button dataTest;
    Button send;
    Button btnlocation, findAddressLocation, findDistTime, tryButton, pushNotification;
    private FirebaseDatabase myRef;
    private String userID;
    FirebaseDatabase database;
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    String eName;
    static String LoggedIn_User_Email;
    String value;
    ArrayList<String> fList;
    String eventN;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_event);
        Intent intent = this.getIntent();
        eventN = intent.getStringExtra("name");

        OneSignal.startInit(this).init();
        send = (Button) findViewById(R.id.send);
        // Call syncHashedEmail anywhere in your app if you have the user's email.
        // This improves the effectiveness of OneSignal's "best-time" notification scheduling feature.
        // OneSignal.syncHashedEmail(userEmail);

        //////////////////Setting the tags for Current User./////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        LoggedIn_User_Email =user.getEmail();
        // LoggedIn_User_Email = "handeozyazgan@gmail.com";
        OneSignal.sendTag("User_ID", LoggedIn_User_Email);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        fList = new ArrayList<>();
        userID = user.getUid();

        myRef =FirebaseDatabase.getInstance();
        dataRef = myRef.getReference("Users");
        dataRef.child(userID).child("Friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    value = child.getValue(String.class);
                    fList.add(value);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                for (int i = 0; i<fList.size(); i++){
                    sendNotification(fList.get(i));
                }

            }
        });
    }
    private void sendNotification(final String mail)
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email="bengu.akbostanci06@gmail.com";

                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    if (SendEvent.LoggedIn_User_Email.equals("bengu.akbostanci06@gmail.com")) {
                            send_email = mail;

                       // send_email = "handeozyazgan@gmail.com";
                    } else {
                        send_email = "bengu.akbostanci06@gmail.com";
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
                                + "\"contents\": {\"en\": \""+eventN+"\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);
                       // httpResponse >= HttpURLConnection.HTTP_OK
                          //      && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST)

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            dataRef.child(userID).child("activities").child("Event").setValue(eventN);
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else{
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
