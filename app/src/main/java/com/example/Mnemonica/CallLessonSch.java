package com.example.Mnemonica;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Bengu on 3.5.2017.
 */

public class CallLessonSch extends Activity {
    Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create the Handler object (on the main thread by default)
        handler = new Handler();
// Define the code block to be executed

// Start the initial runnable task by posting through the handler
        handler.post(runnableCode);
    }
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.d("Handlers", "Called on main thread");
            Intent intent = new Intent(CallLessonSch.this, LessonSchedule.class);
            startActivity(intent);
            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(runnableCode, 2000);
        }
    };
}

