package com.example.Mnemonica;

/**
 * Created by Bengu on 16.3.2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;



public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("str");
        String key = intent.getStringExtra("key");
        String dest = intent.getStringExtra("dest");
        //String len = intent.getStringExtra("len");
        Toast.makeText(context,action, Toast.LENGTH_LONG).show();


        Intent intent2 = new Intent(context,Pop.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.putExtra("str",action);
        intent2.putExtra("key",key);
        intent2.putExtra("dest",dest);
        Toast.makeText(context,"Alarm receiverdan geldim, "+dest, Toast.LENGTH_LONG).show();
        // intent2.putExtra("len",len);
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