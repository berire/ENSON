package com.example.Mnemonica;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Bengu on 5.5.2017.
 */

public class ExamCheck extends Activity {
    boolean isClicked = true;
    PopupWindow popUpWindow;
    ViewGroup.LayoutParams layoutParams;
    LinearLayout mainLayout;
    Button btnClickHere;
    LinearLayout containerLayout;
    String str;
    String key;
    String len;
    TextView tvMsg;
    TextView tevet;
    Ringtone ringtone;
    Schedule s = new Schedule();
    String destination;
    int hour;
    String keyStr;
    int minute;
    int year;
    int month;
    int day;
    String name;
    int actLstSize;
    private FirebaseAuth mAuth;


    private DatabaseReference dataRef;

    private FirebaseDatabase myRef;
    private String userID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_exam);

        //tevet = (TextView)findViewById(R.id.eventTitle);
        Intent intent1 = this.getIntent();
        destination = intent1.getStringExtra("dest");
        name = intent1.getStringExtra("name");
        hour = intent1.getIntExtra("hour",0);
        minute = intent1.getIntExtra("minute",0);
        month = intent1.getIntExtra("month",0);
        year = intent1.getIntExtra("year",0);
        day = intent1.getIntExtra("day",0);
        keyStr = intent1.getStringExtra("key");
        actLstSize = intent1.getIntExtra("size",0);
        // len = intent.getStringExtra("len");


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef =FirebaseDatabase.getInstance();
        dataRef = myRef.getReference("Users");

       /* containerLayout = new LinearLayout(this);
        mainLayout = new LinearLayout(this);
        popUpWindow = new PopupWindow(this);


        btnClickHere = new Button(this);
        btnClickHere.setText("Click Here For Pop Up Window !!!");
        btnClickHere.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (isClicked) {
                    isClicked = false;
                    popUpWindow.showAtLocation(mainLayout, Gravity.BOTTOM, 10, 10);
                    popUpWindow.update(50, 50, 320, 90);
                } else {
                    isClicked = true;
                    popUpWindow.dismiss();
                }
            }

        });

        tvMsg = new TextView(this);
        tvMsg.setText("Hi this is pop up window...");

        layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
        containerLayout.addView(tvMsg, layoutParams);
        popUpWindow.setContentView(containerLayout);
        mainLayout.addView(btnClickHere, layoutParams);
        setContentView(mainLayout); */

        AlertDialog.Builder builder = new AlertDialog.Builder(ExamCheck.this);
        builder.setTitle("Mnemonica");
        builder.setMessage(name+" "+ destination+" "+" "+hour+":"+minute+" "+day+"/"+month+"/"+year);
        builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {

                //İptal butonuna basılınca yapılacaklar.Sadece kapanması isteniyorsa boş bırakılacak
                //ringtone.stop();

            }
        });


        builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Tamam butonuna basılınca yapılacaklar
                dataRef.child(userID).child("Exams").child(keyStr).removeValue();
                //dataRef.child(userID).child("Number of Activities").setValue(len);
                Intent intent = new Intent(ExamCheck.this,Menu.class);
                startActivity(intent);


            }
        });


        builder.show();
        addNotification();

    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.m_ic)
                        .setContentTitle("Mnemonica")
                        .setContentText(name+" "+ destination+" "+" "+hour+":"+minute+" "+day+"/"+month+"/"+year);

        Intent notificationIntent = new Intent(this, Pop.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());



     /*   Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(Pop.this, alarmUri);
        ringtone.play();*/

    }

  /*  @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*8),(int)(height*6));


    }*/
}