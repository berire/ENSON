package com.example.Mnemonica;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Mnemonica.TimeBetweenLocations.MapsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by user on 5.5.2017.
 */

public class Hotel extends AppCompatActivity {

    private Button add_friend,view_friend,send_mes,view_reqs;
    private ImageView background;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

    //     background = (ImageView) findViewById(R.id.background_hotel);

        add_friend=(Button) findViewById(R.id.add_friend);
        add_friend.setVisibility(View.VISIBLE);
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Hotel.this,AddFriend.class);
                startActivity(intent);

            }
        });


       view_friend=(Button) findViewById(R.id.view_friend);
        view_friend.setVisibility(View.VISIBLE);
        view_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Hotel.this,ShowFriends.class);
                startActivity(intent);

            }
        });

        send_mes=(Button) findViewById(R.id.send_mes);
        send_mes.setVisibility(View.VISIBLE);
        send_mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Hotel.this,Users.class);
                startActivity(intent);

            }
        });


        view_reqs=(Button) findViewById(R.id.view_reqs);
        view_reqs.setVisibility(View.VISIBLE);
        view_reqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Hotel.this,View_Request.class);
                startActivity(intent);

            }
        });




    }
}
