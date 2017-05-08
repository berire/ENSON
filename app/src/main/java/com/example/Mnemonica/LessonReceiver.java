package com.example.Mnemonica;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Bengu on 29.4.2017.
 */

public class LessonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String destination = intent.getStringExtra("dest");
        String name = intent.getStringExtra("name");
        int hour = intent.getIntExtra("hour",0);
        int minute = intent.getIntExtra("minute",0);
        int month = intent.getIntExtra("month",0);
        int year = intent.getIntExtra("year",0);
        int day = intent.getIntExtra("day",0);
        int absence = intent.getIntExtra("absence",0);
        int attendanceLimit = intent.getIntExtra("attendanceLimit",0);
        String keyStr = intent.getStringExtra("key");
        int actLstSize = intent.getIntExtra("size", 0);
        //Toast.makeText(context,action, Toast.LENGTH_LONG).show();


        Intent intent2 = new Intent(context,CheckLessonLoc.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.putExtra("dest", destination);
        intent2.putExtra("name", name);
        intent2.putExtra("hour", hour);
        intent2.putExtra("minute", minute);
        intent2.putExtra("month", month);
        intent2.putExtra("year", year);
        intent2.putExtra("key", keyStr);
        intent2.putExtra("day",day);
        intent2.putExtra("absence",absence);
        intent2.putExtra("attendanceLimit",attendanceLimit);
        intent2.putExtra("size", actLstSize);
        context.startActivity(intent2);
        /*
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();*/
    }
}