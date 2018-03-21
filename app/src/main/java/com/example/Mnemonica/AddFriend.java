package com.example.Mnemonica;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Bengu on 17.4.2017.
 */

public class AddFriend extends Activity {
    private DatabaseReference dataRef;
    private DatabaseReference nRef;
    private FirebaseDatabase myRef;
    private String userID;
    private String key;
    private String value;
    private String activityID;
    private FirebaseDatabase mFirebaseDatabase;
    private ArrayList<String> uIDList;
    private FirebaseAuth mAuth;
    private ArrayList<String> friendsList;
    private ListView friendsView;
    private static int friendNum = 0;
    ArrayAdapter<String> arrAdap;
    private SearchView searchF;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef =FirebaseDatabase.getInstance();
        dataRef = myRef.getReference("Users");

        friendsList = new ArrayList<>();
        uIDList = new ArrayList<>();

        searchF = (SearchView) findViewById(R.id.searchF);
        friendsView = (ListView) findViewById(R.id.friendsView);
        arrAdap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendsList);
        friendsView.setAdapter(arrAdap);

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    key = child.getKey();
                    uIDList.add(key);
                    value = child.child("Mail").getValue(String.class);
                    //arrAdap.notifyDataSetChanged();
                    //for (DataSnapshot ch : child.getChildren()) {
                      //  value = ch.getValue(String.class);
                      //  key = ch.getKey();
                      //  if (key.equals("Mail")){
                        //value = child.child("Mail").getValue(String.class);
                    friendsList.add(value);
                    arrAdap.notifyDataSetChanged();
                       // }
                   // }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        friendsView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(AddFriend.this);
                adb.setTitle("Send Friendship Request");
                adb.setMessage("Are you sure you want to send request" + position);
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Send", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Send_Request.Request_Type=1;
                        Send_Request.Event_id="q";
                        Send_Request.toWhom=uIDList.get(positionToRemove);
                        friendsList.remove(positionToRemove);
                        Intent intent = new Intent(AddFriend.this, Send_Request.class);
                        startActivity(intent);

                    }});
                adb.show();
            }
        });
    }
}
