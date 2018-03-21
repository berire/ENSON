package com.example.Mnemonica;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

/**
 * Created by Bengu on 29.4.2017.
 */

public class ViewActivities extends Activity{
    private Button allActivities;
    private Button lessonActs;
    private Button examActs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activities);

        init();

        allActivities.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewActivities.this, ActList.class);
                startActivity(intent);
            }
        });

        lessonActs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewActivities.this, LessonList.class);
                startActivity(intent);
            }
        });

        examActs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewActivities.this, ExamList.class);
                startActivity(intent);
            }
        });
    }
    private void init(){
        allActivities = (Button) findViewById(R.id.allActivities);
        lessonActs = (Button) findViewById(R.id.lessonActs);
        examActs = (Button) findViewById(R.id.examActs);
    }
}
