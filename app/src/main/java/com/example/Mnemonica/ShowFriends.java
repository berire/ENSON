package com.example.Mnemonica;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Bengu on 17.4.2017.
 */

public class ShowFriends extends Activity {

    private ArrayList<String> mUsernames;
    private ArrayList<String> userClone;
    FirebaseDatabase database;

    private ListView friendList;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference dataRef;
    private FirebaseDatabase myRef;
    private String userID;
    private String activityID;
    String key;
    String key2;
    ArrayAdapter<String> arrAdap;
    ArrayList<String> friends;
    ArrayList<String> friendNo;
    boolean check =true;
    String value;
    int num=0;
    int no;
    int i=1;
    int newNum;

    private ListView mListView;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_friends);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef =FirebaseDatabase.getInstance();
        dataRef = myRef.getReference("Users");

        friends = new ArrayList<>();
        userClone = new ArrayList<>();
        friendNo = new ArrayList<>();

        friendList = (ListView) findViewById(R.id.friendList);
        arrAdap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friends);
        friendList.setAdapter(arrAdap);
        num++;
        activityID = String.valueOf(num);
        key=activityID;
        key2=activityID;

      /*  dataRef.child(userID).child("Number of Activities").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                no = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/



        dataRef.child(userID).child("Friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               // for (DataSnapshot child : dataSnapshot.getChildren()){
                    String no = dataSnapshot.getKey();
                    friendNo.add(no);
                    value = dataSnapshot.getValue(String.class);
                    userClone.add(value);
                    friends.add(value);
                    arrAdap.notifyDataSetChanged();
                //}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder adb=new AlertDialog.Builder(ShowFriends.this);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + i);
                final int positionToRemove = i;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int c = userClone.indexOf(friends.get(positionToRemove));
                        String b = friendNo.get(c);
                        dataRef.child(userID).child("Friends").child(b).removeValue();
                        friends.remove(positionToRemove);
                        arrAdap.notifyDataSetChanged();
                    }});
                adb.show();
            }

        });

        //arrAdap.notifyDataSetChanged();
    }
}
