package com.example.Mnemonica.TimeBetweenLocations;

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

import com.example.Mnemonica.Act;
import com.example.Mnemonica.ActList;
import com.example.Mnemonica.AddActToSchedule;
import com.example.Mnemonica.AlarmReceiver;
import com.example.Mnemonica.Constants;
import com.example.Mnemonica.GPSTracker;
import com.example.Mnemonica.GeocodeAddressIntentService;
import com.example.Mnemonica.Menu;
import com.example.Mnemonica.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GPSTracker gps;
    AddressResultReceiver mResultReceiver;
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
    String name;
    ArrayList<String> sss;
    String value;
    int num;
    String key="";
    String key2="";
    ArrayList<String> keys;
    static ArrayList<Act> actList;
    static int lstSize = 0;
    int actLstSize;
    ArrayList<String> time;
    int arTime = 0;
    AddActToSchedule at;
    ActList ac;
    String timeStr;

//bu kilasda bizim sirali aktivite listesinden 1.sini alicaz ve onun locationini kullanarak kisinin lokasyonu ile arsindaki zaman farkini hesapliycaz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MultiDex.install(this);*/

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

        // sss = intent1.getStringArrayListExtra("list");

        //actList = (ArrayList<Act>) intent1.getSerializableExtra("list");
        // startAL();


        ////////////////////////////////////////////////////////////////////////////////////////////


  /*      dataRef.child(userID).child("activities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    if(child.getKey().equals("Activity Destination")){
                        destination = child.getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


*/



        ////////////////////////////////////////////////////////////////////////////////////////////
        // for (int y=0; y<sss.size(); y=y+7) {
        fetchType = Constants.USE_ADDRESS_NAME;
        mResultReceiver = new AddressResultReceiver(null);
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_NAME_DATA_EXTRA, destination);
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);

        Log.e(TAG, "Starting Service");
        startService(intent);
        //}


        ////////////////////////////////////////////////////////////////////////////////////////////




 /*       gps = new GPSTracker(MapsActivity.this);

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

        //////////////////////////////////////////////////////////////////////////

        LatLng firstLatLng=new LatLng(latitude,longitude);
        LatLng sectLatLng=new LatLng(latitude2,longitude2);
        CalculateDistanceTime distance_task = new CalculateDistanceTime(MapsActivity.this);
        distance_task.getDirectionsUrl(firstLatLng, sectLatLng);

        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
            @Override
            public void taskCompleted(String[] time_distance) {
                Toast.makeText(MapsActivity.this, time_distance[1],Toast.LENGTH_LONG).show();
                Toast.makeText(MapsActivity.this, time_distance[0],Toast.LENGTH_LONG).show();
            }
        });*/
    }

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
                        latitude2 = address.getLatitude();
                        longitude2 = address.getLongitude();
                        Toast.makeText(MapsActivity.this, "Latitude: " + address.getLatitude() + "\n" +
                                "Longitude: " + address.getLongitude() + "\n" + destination + "\n" +
                                "Address: " + resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();

                        gps = new GPSTracker(MapsActivity.this);

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
                        CalculateDistanceTime distance_task = new CalculateDistanceTime(MapsActivity.this);
                        distance_task.getDirectionsUrl(firstLatLng, sectLatLng);
                        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {

                            @Override
                            public void taskCompleted(String[] time_distance) {
                                int index = time_distance[1].indexOf(" ");
                                timeStr = time_distance[1].substring(0,index);
                                arTime = Integer.valueOf(timeStr);
                                // String l = "20";
                                // arTime = Integer.valueOf(l);
                                //arTime = arTime - 4;
                                //timeStr = String.valueOf(arTime);
                                time.add(timeStr);
                                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                                Toast.makeText(MapsActivity.this, time_distance[1]+ " "+ timeStr,Toast.LENGTH_LONG).show();
                                Toast.makeText(MapsActivity.this, time_distance[0],Toast.LENGTH_LONG).show();
                                if(arTime<=minute && !(minute==0)){
                                    minute = minute - arTime;
                                }
                                else{
                                    minute = 60 + (minute-arTime);
                                    hour = hour - 1;
                                }
                                //startAL();
                                Act act = new Act();
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
                                }
                            }
                        });

                    }


                });
                //////////////////////////////////////////////////////////////////////////



            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MapsActivity.this, resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();
                    }
                });
            }

           /* LatLng firstLatLng=new LatLng(latitude,longitude);
            LatLng sectLatLng=new LatLng(latitude2,longitude2);
            CalculateDistanceTime distance_task = new CalculateDistanceTime(MapsActivity.this);
            distance_task.getDirectionsUrl(firstLatLng, sectLatLng);

            distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                @Override
                public void taskCompleted(String[] time_distance) {
                    Toast.makeText(MapsActivity.this, time_distance[1],Toast.LENGTH_LONG).show();
                    Toast.makeText(MapsActivity.this, time_distance[0],Toast.LENGTH_LONG).show();
                }
            });*/
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
    private void strAl() {
        Toast.makeText(MapsActivity.this, "Set Up Alarm",Toast.LENGTH_LONG).show();

        /*for (int k=0; k<actList.size(); k++){
            String tm = time.get(k);

            //arTime =  parse(tm).intValue();
                    //Integer.valueOf(time.get(k));
            int min = actList.get(k).getMinute();
            int h = actList.get(k).getHour();
            if(arTime<= min){
                min = min - arTime;
                actList.get(k).setMinute(min);
            }
            else{
                min = 60 + (min-arTime);
                h = h - 1;
                actList.get(k).setMinute(min);
                actList.get(k).setHour(h);
            }
        }*/


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
            Intent intent = new Intent(MapsActivity.this,
                    AlarmReceiver.class);
            intent.putExtra("str",actList.get(f).getActName());
            intent.putExtra("key",actList.get(f).getKey());

            //intent.putExtra("len", actNum+"");

            PendingIntent pi = PendingIntent.getBroadcast(
                    MapsActivity.this, f, intent, 0);

            alarmManager[f] = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager[f].set(AlarmManager.RTC_WAKEUP,
                    cal[f].getTimeInMillis(), pi);

            intentArray.add(pi);
            // actList.remove(f);
        }
        alarmManager[0]=null;
        actList.clear();
        lstSize = 0;

        Intent intnt = new Intent(MapsActivity.this,Menu.class);
        startActivity(intnt);
    }
}