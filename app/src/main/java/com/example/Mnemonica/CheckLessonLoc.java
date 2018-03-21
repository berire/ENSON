package com.example.Mnemonica;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.Mnemonica.TimeBetweenLocations.CalculateDistanceTime;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Bengu on 29.4.2017.
 */

public class CheckLessonLoc extends FragmentActivity implements OnMapReadyCallback {

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
    int distance;
    String distanceStr;
    static int attendence = 0;
    FirebaseUser user;
    DatabaseReference myRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_less);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MultiDex.install(this);
        Firebase.setAndroidContext(this);
        */

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        sss = new ArrayList<>();
        actList = new ArrayList<>();
        keys = new ArrayList<>();
        //actList = new ArrayList<>();
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

        Toast.makeText(CheckLessonLoc.this, "Absence: "+ absence,Toast.LENGTH_LONG).show();

        ////////////////////////////////////////////////////////////////////////////////////////////
        // for (int y=0; y<sss.size(); y=y+7) {
        fetchType = Constants.USE_ADDRESS_NAME;
        mResultReceiver = new AddressResultReceiver(null);
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, "Bilkent Üniversitesi");
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
                        Toast.makeText(CheckLessonLoc.this, "Latitude: " + address.getLatitude() + "\n" +
                                "Longitude: " + address.getLongitude() + "\n" + destination + "\n" +
                                "Address: " + resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();

                        gps = new GPSTracker(CheckLessonLoc.this);

                        // Check if GPS enabled
                        if(gps.canGetLocation()) {

                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();

                            // \n is for new line
                            //  Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                        } else {
                            // Can't get location.
                            // GPS or network is not enabled.
                            // Ask user to enable GPS/network in settings.
                            gps.showSettingsAlert();
                        }

                        LatLng firstLatLng=new LatLng(latitude,longitude);
                        LatLng sectLatLng=new LatLng(latitude2,longitude2);
                        CalculateDistanceTime distance_task = new CalculateDistanceTime(CheckLessonLoc.this);
                        distance_task.getDirectionsUrl(firstLatLng, sectLatLng);
                        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {

                            @Override
                            public void taskCompleted(String[] time_distance) {
                                int index = time_distance[1].indexOf(" ");
                                distanceStr = time_distance[0].substring(0,index);
                                distance = Integer.valueOf(distanceStr);
                                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                                Toast.makeText(CheckLessonLoc.this, time_distance[1]+ " "+ timeStr,Toast.LENGTH_LONG).show();
                                Toast.makeText(CheckLessonLoc.this, time_distance[0],Toast.LENGTH_LONG).show();
                                if(distance>5){
                                    absence++;
                                    // Toast.makeText(CheckLessonLoc.this, "Ansence: "+ absence,Toast.LENGTH_LONG).show();
                                    if(absence==attendanceLimit){
                                        Toast.makeText(CheckLessonLoc.this, "Ansence: "+ absence+"\n"+"You Fail!",Toast.LENGTH_LONG).show();
                                    }
                                }


                                Toast.makeText(CheckLessonLoc.this, "Absence: "+ absence,Toast.LENGTH_LONG).show();
                                String a = String.valueOf(absence);
                                DatabaseReference newRef = myRef.child("Users").child(uid).child("Lessons").child(keyStr);
                                newRef.child("Absence").setValue(a);

                                //startAL();
                             /*   Act act = new Act();
                                act.setHour(hour);
                                act.setActName(name);
                                act.setDate(day);
                                act.setMinute(minute);
                                act.setMonth(month);
                                act.setYear(year);
                                act.setDestination(destination);
                                act.setKey(keyStr);
                                actList.add(act);
                                lstSize++;
                                if(lstSize==actLstSize){
                                    strAl();
                                }*/
                            }
                        });

                    }


                });
                //////////////////////////////////////////////////////////////////////////



            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CheckLessonLoc.this, resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();
                    }
                });
            }
            startAL();
        }
    }
    private void startAL(){
        Intent intnt = new Intent(CheckLessonLoc.this, Menu.class);
        startActivity(intnt);

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
    private void strAl() {
        Toast.makeText(CheckLessonLoc.this, "Set Up Alarm",Toast.LENGTH_LONG).show();
        Calendar cal[] = new Calendar[actList.size()];

        for (int i = 0; i < actList.size(); i++) {

            cal[i] = Calendar.getInstance();
            cal[i].set(Calendar.HOUR_OF_DAY, actList.get(i).getHour());
            cal[i].set(Calendar.MINUTE, (actList.get(i).getMinute()));
            cal[i].set(Calendar.SECOND, 0);
            cal[i].set(Calendar.DAY_OF_MONTH, actList.get(i).getDate());
            cal[i].set(Calendar.MONTH, actList.get(i).getMonth());
            cal[i].set(Calendar.YEAR, actList.get(i).getYear());
        }
        //int actNum =keys.size();
        AlarmManager[] alarmManager = new AlarmManager[actList.size()];
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
        for (int f = 0; f < cal.length; f++) {
            //   actNum=actNum-1;
            Intent intent = new Intent(CheckLessonLoc.this,
                    AlarmReceiver.class);
            intent.putExtra("str",actList.get(f).getActName());
            intent.putExtra("key",actList.get(f).getKey());
            //intent.putExtra("len", actNum+"");

            PendingIntent pi = PendingIntent.getBroadcast(
                    CheckLessonLoc.this, f, intent, 0);

            alarmManager[f] = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager[f].set(AlarmManager.RTC_WAKEUP,
                    cal[f].getTimeInMillis(), pi);

            intentArray.add(pi);
            // actList.remove(f);
        }
        alarmManager[0]=null;
        actList.clear();
        lstSize = 0;
    }
}