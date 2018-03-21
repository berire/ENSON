package com.example.Mnemonica;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by handeozyazgan on 28/04/17.
 */

public class Try extends Activity {
    ArrayList<Act> actions;
    ArrayList<String> name;
    private FirebaseAuth mAuth;
    private DatabaseReference dataRef;
    private FirebaseDatabase myRef;
    private String userID;
    private DatabaseReference mPostReference;
    ArrayList<String> Actids=new ArrayList<>();
    String anid;
    String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tryfortry);

        Firebase.setAndroidContext(this);

        actions=new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef =FirebaseDatabase.getInstance();
        dataRef = myRef.getReference("Users");

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference( )
                .child("Users").child(userID).child("activities");

    }
    @Override
    public void onStart() {
        super.onStart();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()){
                    anid=(String)child.getKey();
                    Actids.add(anid);
                }
                for (int i=0;i<Actids.size();i++)
                {
                    Act act=new Act();
                    String aKey= Actids.get(i);
                    value=(String) dataSnapshot.child(aKey).child("Activity Name").getValue();
                    act.setActName(value);
                    Toast.makeText(Try.this,act.getActName(), Toast.LENGTH_LONG).show();
                    //act.setDate(Integer.parseInt(((String) dataSnapshot.child(aKey).child("Activity Day").getValue()).toString()));

                   // act.setMonth(Integer.parseInt(( (String) dataSnapshot.child(aKey).child("Activity Month").getValue()).toString()));
                   // act.setDestination((((String) dataSnapshot.child(aKey).child("Activity Destination").getValue()).toString()));
                  //  act.setHour(Integer.parseInt(((String) dataSnapshot.child(aKey).child("Activity Hour").getValue()).toString()));
                  //  act.setYear(Integer.parseInt(((String) dataSnapshot.child(aKey).child("Activity Year").getValue()).toString()));
                 //   act.setMinute(Integer.parseInt(((String) dataSnapshot.child(aKey).child("Activity Minute").getValue()).toString()));
                    actions.add(act);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // [START_EXCLUDE]
                Toast.makeText(Try.this, "Failed to load activities",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mPostReference.addValueEventListener(postListener);
        Set<Act> hs = new HashSet<>();
        hs.addAll(actions);
        actions.clear();
        actions.addAll(hs);

    }
}
