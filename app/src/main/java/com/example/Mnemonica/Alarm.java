package com.example.Mnemonica;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

/**
 * Created by Bengu on 16.3.2017.
 */

public class Alarm extends Activity {
    Button alamBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        final Ringtone ringtone = RingtoneManager.getRingtone(Alarm.this, alarmUri);
        ringtone.play();

        alamBtn = (Button) findViewById(R.id.alarmBtn);

        alamBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ringtone.stop();
            }
        });
    }
}
