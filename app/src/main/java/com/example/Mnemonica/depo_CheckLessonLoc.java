package com.example.Mnemonica;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.Mnemonica.TimeBetweenLocations.CalculateDistanceTime;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Bengu on 29.4.2017.
 */

public class depo_CheckLessonLoc extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GPSTracker gps;
    CheckLessonLoc.AddressResultReceiver mResultReceiver;
    private static final String TAG = "Addres_Location";
    int fetchType = Constants.USE_ADDRESS_LOCATION;
    double latitude;
    double longitude;
    double latitude2;
    double longitude2;

    String destination;
    int hour;
    String keyStr;
    int minute;
    int year;
    int month;
    int day;
    int attendanceLimit;
    int absence;
    String name;
    ArrayList<String> sss;
    String value;
    String uid;
    String key="";
    String key2="";
    ArrayList<String> keys;
    static ArrayList<Act> actList;
    static int lstSize = 0;
    int actLstSize;
    ArrayList<String> time;
    AddActToSchedule at;
    ActList ac;
    String timeStr;
    long distance;
    String distanceStr;
    static int attendence = 0;
    FirebaseUser user;
    DatabaseReference myRef;
    FirebaseDatabase database;


    String value11;


    private FirebaseAuth mAuth2;
    private DatabaseReference dataRef2;
    private FirebaseDatabase myRef2;
    private String userID2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklessonloc);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MultiDex.install(this);
        Firebase.setAndroidContext(this);


        mAuth2 = FirebaseAuth.getInstance();
        final FirebaseUser user2 = mAuth2.getCurrentUser();
        userID2 = user2.getUid();
        myRef2 =FirebaseDatabase.getInstance();
        dataRef2 = myRef2.getReference("Users");

        dataRef2.child(userID2).child("Lessons").child("keyStr").child("Absence").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    value11 = child.getValue(String.class);
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        sss = new ArrayList<>();
        actList = new ArrayList<>();
        keys = new ArrayList<>();
        at = new AddActToSchedule();
        ac = new ActList();
        time = new ArrayList<>();

        Intent intent1 = this.getIntent();
        destination = intent1.getStringExtra("dest");
        name = intent1.getStringExtra("name");
        hour = intent1.getIntExtra("hour",0);
        minute = intent1.getIntExtra("minute",0);
        month = intent1.getIntExtra("month",0);
        year = intent1.getIntExtra("year",0);
        day = intent1.getIntExtra("day",0);
        keyStr = intent1.getStringExtra("key");
        actLstSize = intent1.getIntExtra("size",0);
        absence = intent1.getIntExtra("absence",0);
        attendanceLimit = intent1.getIntExtra("attendanceLimit",0);


        ////////////////////////////////////////////////////////////////////////////////////////////
        // for (int y=0; y<sss.size(); y=y+7) {
        fetchType = Constants.USE_ADDRESS_NAME;
        //!!!!!!!!!aÃ§ burayi!!!!!!!  mResultReceiver = new AddressResultReceiver(null);
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, "Armada");
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);

        Log.e(TAG, "Starting Service");
        startService(intent);
        //}

        ////////////////////////////////////////////////////////////////////////////////////////////

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {


            if (resultCode == Constants.SUCCESS_RESULT) {

                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        latitude2 = address.getLatitude();
                        longitude2 = address.getLongitude();
                        Toast.makeText(depo_CheckLessonLoc.this, "Latitude: " + address.getLatitude() + "\n" +
                                "Longitude: " + address.getLongitude() + "\n" + destination + "\n" +
                                "Address: " + resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();

                        gps = new GPSTracker(depo_CheckLessonLoc.this);

                        // Check if GPS enabled
                        if(gps.canGetLocation()) {

                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();

                        } else {
                            gps.showSettingsAlert();
                        }

                        LatLng firstLatLng=new LatLng(latitude,longitude);
                        LatLng sectLatLng=new LatLng(latitude2,longitude2);
                        CalculateDistanceTime distance_task = new CalculateDistanceTime(depo_CheckLessonLoc.this);
                        distance_task.getDirectionsUrl(firstLatLng, sectLatLng);
                        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {

                            @Override
                            public void taskCompleted(String[] time_distance) {
                                int index = time_distance[1].indexOf(" ");
                                distanceStr = time_distance[0].substring(0,index);
                                distance = 4;
                                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                                Toast.makeText(depo_CheckLessonLoc.this, time_distance[1]+ " "+ timeStr,Toast.LENGTH_LONG).show();
                                Toast.makeText(depo_CheckLessonLoc.this, time_distance[0],Toast.LENGTH_LONG).show();
                                if(distance>3){
                                    absence++;
                                    Toast.makeText(depo_CheckLessonLoc.this, "Absence: "+ absence,Toast.LENGTH_LONG).show();
                                }
                                Toast.makeText(depo_CheckLessonLoc.this, " is absence number",Toast.LENGTH_LONG);

                                Toast.makeText(depo_CheckLessonLoc.this, "Absence: "+ absence,Toast.LENGTH_LONG).show();
                                String a = String.valueOf(absence);
                                DatabaseReference newRef = myRef.child("Users").child(uid).child("Lessons").child(keyStr);
                                newRef.child("Absence").setValue(a);

                            }
                        });

                    }


                });
                //////////////////////////////////////////////////////////////////////////



            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(depo_CheckLessonLoc.this, resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();
                    }
                });
            }

        }
    }
    private void startAL(){

      /*  Toast.makeText(MapsActivity.this, "bengu",Toast.LENGTH_LONG).show();
        Toast.makeText(MapsActivity.this, name,Toast.LENGTH_LONG).show();
        Toast.makeText(MapsActivity.this, destination,Toast.LENGTH_LONG).show();
        Toast.makeText(MapsActivity.this, String.valueOf(hour),Toast.LENGTH_LONG).show();
        Toast.makeText(MapsActivity.this, String.valueOf(minute),Toast.LENGTH_LONG).show();
        Toast.makeText(MapsActivity.this, String.valueOf(year),Toast.LENGTH_LONG).show();
        Toast.makeText(MapsActivity.this, String.valueOf(month),Toast.LENGTH_LONG).show();
        Toast.makeText(MapsActivity.this, keyStr,Toast.LENGTH_LONG).show();*/
       /* for (int i =0; i<sss.size(); i++){
          Toast.makeText(MapsActivity.this, sss.get(i),Toast.LENGTH_LONG).show();
        }*/
     /* for (int i =0; i<sss.size(); i++){
          Toast.makeText(MapsActivity.this, actList.get(i).getActName(),Toast.LENGTH_LONG).show();
      }*/
        //  Intent intent3 = new Intent(MapsActivity.this, AddActToSchedule.class);
        //  startActivity(intent3);
    }

}