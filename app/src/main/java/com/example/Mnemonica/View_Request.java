package com.example.Mnemonica;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 7.5.2017.
 */

public class View_Request extends AppCompatActivity {

    private String userID;
    private FirebaseAuth mAuth;
    public static String toWhom="";
    public static int Request_Type=0;


    private DatabaseReference mPostReference;
    private ArrayList<String> requests_in=new ArrayList<>();
    private ArrayList<String> r_in_id=new ArrayList<>();
    private ArrayList<String> requests_out=new ArrayList<>();
    private ArrayList<String> Reqids=new ArrayList<>();
    ArrayAdapter<String> arrAdap_in;
    ArrayAdapter<String> arrAdap_out;
    private ListView request_list_in;
    private ListView request_list_out;

    private static int friendNum = 0;

    private DatabaseReference dataRef;
    private FirebaseDatabase myRef;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vivew_request);

        request_list_in = (ListView) findViewById(R.id.view_list1);
        arrAdap_in = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,requests_in);
        request_list_in.setAdapter(arrAdap_in);


        request_list_out = (ListView) findViewById(R.id.view_list2);
        arrAdap_out = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,requests_out);
        request_list_out.setAdapter(arrAdap_out);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();


        myRef =FirebaseDatabase.getInstance();
        dataRef = myRef.getReference("Users");

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference( )
                .child("Request");

        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String anid = (String) child.getKey();
                    Reqids.add(anid);
                }

                for(int i=0;i<Reqids.size();i++)
                {
                    String aKey= Reqids.get(i);
                    for (DataSnapshot child : dataSnapshot.child(aKey).getChildren()) {
                        String achild = (String) child.getKey();
                        String mes="";
                        long request=(long)dataSnapshot.child(aKey).child(achild).child("Request Type").getValue();
                        String toW=(String) dataSnapshot.child(aKey).child(achild).child("To Whom").getValue();
                        String [] fgt=Reqids.get(i).split("_");
                        String AID = fgt[0];
                        String BID=fgt[1];
//                        String CID=fgt[2];
                        if(userID.contains(AID)){
                            if(request==(long)1)
                            {
                                mes="Friendship Request";
                            }
                            if(request==(long)2)
                            {

                                mes="Activity Request";
                            }

                            if(request==(long)0)
                            {
                                mes="FAIL";
                            }

                            requests_out.add("You Send: "+mes+ "To: "+BID);
                        }

                        int bln=-1;
                        if(userID.contains(BID)){
                            if(request==(long)1)
                            {
                                bln=1;
                                mes="Friendship Request";
                            }
                            if(request==(long)2)
                            {
                                bln=2;
                                mes="Activity Request";
                            }

                            if(request==(long)0)
                            {
                                bln=0;
                                mes="FAIL";
                            }

                            requests_in.add("You Got: "+mes+ "From: "+AID);
                            r_in_id.add(AID +"#"+ bln+"#"+aKey+"#"+achild);
                            arrAdap_in.notifyDataSetChanged();
                        }

                    }

                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // [START_EXCLUDE]
                Toast.makeText(View_Request.this, "Failed to load request",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });

        Set<String> hs = new HashSet<>();
        hs.addAll(requests_out);
        requests_out.clear();
        requests_out.addAll(hs);
        arrAdap_out.notifyDataSetChanged();



        request_list_in.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder adb=new AlertDialog.Builder(View_Request.this);
                adb.setTitle("Request");
                adb.setMessage("Approve the request? " + i);
                final int positionToRemove = i;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String abc = r_in_id.get(positionToRemove);

                        String[] parts = abc.split("#");

                        String friend_key= parts[0];
                        String req_type = parts[1];
                        int type=Integer.parseInt(req_type);
                        String req_id = parts[2];
                        String req_child_id = parts[3];
                        //add(AID + "#" + bln + "#" + aKey + "#" + achild);

                        mPostReference.child(req_id).child(req_child_id).child("Is Approved").setValue("YES");

                        if(type==1)
                        {
                            friendNum++;
                            String fNum = String.valueOf(friendNum);
                            dataRef.child(userID).child("Friends").child(fNum).setValue(friend_key);
                            dataRef.child(friend_key).child("Friends").child(fNum).setValue(userID);

                        }
                        if(type==2)
                        {
                            //sendNotification(fList.get(i));
                        }
                        arrAdap_in.notifyDataSetChanged();
                    }});
                adb.show();
            }

        });
    }

}
