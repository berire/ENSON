package com.example.Mnemonica.TimeBetweenLocations;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.Mnemonica.Act;
import com.example.Mnemonica.AddActToSchedule;
import com.example.Mnemonica.Constants;
import com.example.Mnemonica.GPSTracker;
import com.example.Mnemonica.GeocodeAddressIntentService;
import com.example.Mnemonica.R;
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
import java.util.Collections;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GPSTracker gps;
    AddressResultReceiver mResultReceiver;
    private static final String TAG = "Addres_Location";
    int fetchType = Constants.USE_ADDRESS_LOCATION;
    double latitude;
    double longitude;

    //ACTIVITY LIST VARIABLE LIST GETTING ACVTIVITIES FROM DATABASE
    private FirebaseAuth mAuth;
    private DatabaseReference dataRef;
    private FirebaseDatabase myRef;
    private String userID;
    String key="";
    String key2="";
    ArrayAdapter<String> arrAdap;
    ArrayList<Integer> h;
    ArrayList<Integer> min;
    ArrayList<Integer> d;
    ArrayList<Integer> m;
    ArrayList<Integer> y;
    ArrayList<String> name;
    ArrayList<String> sss;
    ArrayList<String> keys;
    ArrayList<Integer> h2;
    ArrayList<Integer> min2;
    ArrayList<Integer> d2;
    ArrayList<Integer> m2;
    ArrayList<Integer> y2;
    ArrayList<String> name2;
    ArrayList<Act> actList;
    ArrayList<Act> actList2;
    ArrayList<Act> actList3;
    ArrayList<Act> actList4;
    ArrayList<Act> actList5;
    String value;
    int num=0;
    AddActToSchedule obj;

//bu kilasda bizim sirali aktivite listesinden 1.sini alicaz ve onun locationini kullanarak kisinin lokasyonu ile arsindaki zaman farkini hesapliycaz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MultiDex.install(this);
        obj = new AddActToSchedule();

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef =FirebaseDatabase.getInstance();
        dataRef = myRef.getReference("Users");
        h = new ArrayList<>();
        min = new ArrayList<>();
        d = new ArrayList<>();
        m = new ArrayList<>();
        y = new ArrayList<>();
        name = new ArrayList<>();
        sss = new ArrayList<>();
        keys = new ArrayList<>();
        h2 = new ArrayList<>();
        min2 = new ArrayList<>();
        d2 = new ArrayList<>();
        m2 = new ArrayList<>();
        y2 = new ArrayList<>();
        name2 = new ArrayList<>();
        actList = new ArrayList<>();
        actList2 = new ArrayList<>();
        actList3 = new ArrayList<>();
        actList4 = new ArrayList<>();
        actList5 = new ArrayList<>();


        dataRef.child(userID).child("activities").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    value = child.getValue(String.class);
                    key = dataSnapshot.getKey();
                    if (!(key.equals(key2))){
                        keys.add(key);
                        key2=key;
                    }
                    sss.add(value);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        for (int j = 0; j < sss.size(); j = j + 7) {
            Act act = new Act();
            act.setHour(Integer.valueOf(sss.get(j + 2)));
            act.setActName(sss.get(j + 5));
            act.setDate(Integer.valueOf(sss.get(j)));
            act.setMinute(Integer.valueOf(sss.get(j + 3)));
            act.setMonth(Integer.valueOf(sss.get(j + 4)));
            act.setYear(Integer.valueOf(sss.get(j + 6)));
            act.setDestination(sss.get(j + 1));
            act.setKey(keys.get(num));
            actList.add(act);
            num++;
        }

        Collections.sort(actList, Act.ActMinuteComp);
        Collections.sort(actList, Act.ActHourComp);
        Collections.sort(actList, Act.ActDateComp);
        Collections.sort(actList, Act.ActMonthComp);


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //FIND ANY COORDINATES and ADDRESS OF ANYWHERE/////////////////////////////////////////////////////////////////////////////////////////
        fetchType = Constants.USE_ADDRESS_NAME;
        mResultReceiver = new AddressResultReceiver(null);
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, "Bilkent");
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);

        Log.e(TAG, "Starting Service");
        startService(intent);
        ////////////////////////////////////////////////////////////////////////////////////////////

        //FINDING THE CURRENT LOCATION/////////////////////////////////////////////////////////////
        gps = new GPSTracker(MapsActivity.this);

        // Check if GPS enabled
        if(gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////

      ////FINDS THE DISTANCE AND MINUTES BETWEEN LOCATIONS//////////////////////////////////////////
        LatLng firstLatLng=new LatLng(latitude,longitude);
        LatLng sectLatLng=new LatLng(39.925054,32.836944);
        CalculateDistanceTime distance_task = new CalculateDistanceTime(MapsActivity.this);
        distance_task.getDirectionsUrl(firstLatLng, sectLatLng);

        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
            @Override
            public void taskCompleted(String[] time_distance) {
                Toast.makeText(MapsActivity.this, time_distance[1],Toast.LENGTH_LONG).show();
                Toast.makeText(MapsActivity.this, time_distance[0],Toast.LENGTH_LONG).show();
            }
        });
    }//ON CREATE BURDA BITTI


    //METHOD SONU/////////////////////////////////////////////

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
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
                        Toast.makeText(MapsActivity.this, "Latitude: " + address.getLatitude() + "\n" +
                                "Longitude: " + address.getLongitude() + "\n" +
                                "Address: " + resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MapsActivity.this, resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}