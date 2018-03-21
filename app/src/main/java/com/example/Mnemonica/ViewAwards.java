package com.example.Mnemonica;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Mnemonica.TimeBetweenLocations.MapsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Bengu on 4.5.2017.
 */

public class ViewAwards extends Activity {
    private ListView awardList;
    private TextView points;
    private TextView maxPoint;
    private TextView winner;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference dataRef;
    private DatabaseReference nRef;
    private FirebaseDatabase myRef;
    ArrayAdapter<String> arrAdap;
    String userID;
    ArrayList<String> awardsLst;
    ArrayList<String> usersLst;
    String maxUser;
    int max=0;
    int aw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_awards);

        points = (TextView)findViewById(R.id.points);
        maxPoint = (TextView) findViewById(R.id.maxPoint);
        winner = (TextView) findViewById(R.id.winner);

        awardsLst = new ArrayList<>();
        usersLst = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef = FirebaseDatabase.getInstance();
        dataRef = myRef.getReference("Users");


        dataRef.child(userID).child("Awards").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                points.setText("Your Award Points: "+value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

      dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String value = child.child("Awards").getValue(String.class);
                    String mail = child.child("Mail").getValue(String.class);
                    //Toast.makeText(ViewAwards.this, value,Toast.LENGTH_LONG).show();
                    if(value==null){

                    }
                    else{
                       // Toast.makeText(ViewAwards.this, value,Toast.LENGTH_LONG).show();
                        aw = Integer.valueOf(value);
                        //Toast.makeText(ViewAwards.this, "aw:"+aw,Toast.LENGTH_LONG).show();
                      //  Toast.makeText(ViewAwards.this, "max: "+max,Toast.LENGTH_LONG).show();
                        if(aw>max){

                            max = aw;
                            maxUser = mail;
                           // Toast.makeText(ViewAwards.this, "aw:"+aw,Toast.LENGTH_LONG).show();
                           // Toast.makeText(ViewAwards.this, "max: "+max,Toast.LENGTH_LONG).show();
                        }
                        //arrAdap.notifyDataSetChanged();
                    }
                    //arrAdap.notifyDataSetChanged();
                    //for (DataSnapshot ch : child.getChildren()) {
                    //  value = ch.getValue(String.class);
                    //  key = ch.getKey();
                    //  if (key.equals("Mail")){
                    //value = child.child("Mail").getValue(String.class);
                   // arrAdap.notifyDataSetChanged();
                    // }
                    // }
                }
                String maxStr = String.valueOf(max);
               // Toast.makeText(ViewAwards.this, "The Max Award Point: "+max,Toast.LENGTH_LONG).show();
                maxPoint.setText("The Max Award Point: "+ maxStr);
                winner.setText("The User Who Has Max Awards Point: " +maxUser);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        //awardsLst.add(Collections.max(awardsP));
       // arrAdap.notifyDataSetChanged();
    }
}
