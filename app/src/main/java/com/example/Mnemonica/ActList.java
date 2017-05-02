package com.example.Mnemonica;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.example.Mnemonica.Act;

/**
 * Created by Bengu on 19.3.2017.
 */

public class ActList extends Activity {

    private TextView mValueView;
    private Firebase mRef;
    private ArrayList<String> mUsernames;
    private ArrayList<String> userClone;
    FirebaseDatabase database;

    private ListView valueList;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference dataRef;
    private DatabaseReference nRef;
    private FirebaseDatabase myRef;
    private String userID;
    private String activityID;
    String key;
    String key2;
    ArrayAdapter<String> arrAdap;
    boolean check =true;
    String value;
    int num=0;
    int no;
    int i=1;
    int newNum;
    ArrayList<Act> a;
    ArrayList<String> sss;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    private ListView mListView;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actlist);

        mUsernames = new ArrayList<>();
        userClone = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef =FirebaseDatabase.getInstance();
        dataRef = myRef.getReference("Users");
        a =  new ArrayList<>();
        sss = new ArrayList<>();


        valueList = (ListView) findViewById(R.id.actList);
        arrAdap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mUsernames);
        valueList.setAdapter(arrAdap);
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
        activityID=String.valueOf(i);


            dataRef.child(userID).child("activities").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        value = child.getValue(String.class);
                        key2 = child.getKey();
                        key = dataSnapshot.getKey();
                        //sss.add(value);
                        if (key2.equals("Activity Name")){
                            userClone.add(key);
                            userClone.add(value);
                            //mUsernames.add(key);
                            mUsernames.add(value);
                            arrAdap.notifyDataSetChanged();
                        }
                    }
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
          //  Collections.sort(mUsernames);
           // arrAdap.notifyDataSetChanged();

  /*     for (int j=0; j<sss.size(); j=j+6){
            Act act = new Act();
            act.setHour(Integer.valueOf(sss.get(j)));
            act.setActName(sss.get(j+1));
            act.setDate(Integer.valueOf(sss.get(j+2)));
            act.setMinute(Integer.valueOf(sss.get(j+3)));
            act.setMonth(Integer.valueOf(sss.get(j+4)));
            act.setYear(Integer.valueOf(sss.get(j+5)));
            act.setKey("");
            a.add(act);
            num++;
        }

        Collections.sort(a, Act.ActMonthComp);
        Collections.sort(a, Act.ActDateComp);
        Collections.sort(a, Act.ActHourComp);
        Collections.sort(a, Act.ActMinuteComp);

        for (int i = 0; i<a.size(); i++){
            mUsernames.add(a.get(i).getActName());
            arrAdap.notifyDataSetChanged();
        }*/

        valueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder adb=new AlertDialog.Builder(ActList.this);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + i);
                final int positionToRemove = i;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int c = userClone.indexOf(mUsernames.get(positionToRemove));
                        int d = c-1;
                        int k = Integer.valueOf(userClone.get(d));
                        String b = String.valueOf(k);
                        dataRef.child(userID).child("activities").child(b).removeValue();
                        mUsernames.remove(positionToRemove);
                        arrAdap.notifyDataSetChanged();
                        int n = mUsernames.size();
                        dataRef.child(userID).child("Number of Activities").setValue(n);
                    }});
                adb.show();
            }

        });

        //arrAdap.notifyDataSetChanged();
    }
}
