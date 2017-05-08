package com.example.Mnemonica;

/**
 * Created by user on 8.5.2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by handeozyazgan on 26/04/17.
 */

public class EventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventN = intent.getStringExtra("eventN");

        Intent myIntent = new Intent(context, EventPop.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.putExtra("eventN",eventN);


    }
}
