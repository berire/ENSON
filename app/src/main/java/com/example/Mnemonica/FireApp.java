package com.example.Mnemonica;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Bengu on 27.3.2017.
 */

public class FireApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
