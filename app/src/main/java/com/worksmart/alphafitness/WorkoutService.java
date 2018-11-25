package com.worksmart.alphafitness;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import android.location.LocationManager;

import java.net.URI;
import java.util.Random;

public class WorkoutService extends Service implements LocationListener {

    private final Integer sampleIntervalMS = 5000;
    private final Boolean useLocation = true;
    private Random random;

    private final IBinder mBinder = new LocalBinder();

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    private Integer count;
    private Integer workoutID;
    public LocationManager locationManager;

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class LocalBinder extends Binder {
        WorkoutService getService() {
            // Return this instance of LocalService so clients can call public methods
            return WorkoutService.this;
        }
    }

    public WorkoutService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
        Log.d("ServiceTest", "Service created!");
        workoutID = -1;

    }

    public void startRunning() {
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                doSample();
                if (workoutID != -1) {

                    handler.postDelayed(runnable, sampleIntervalMS);
                }
            }
        };
        random = new Random();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        doSample();
        handler.postDelayed(runnable, sampleIntervalMS);
    }

    public void doSample() {
        count = count + 1;
        if (workoutID != -1) {
            Integer steps = random.nextInt();
            if (useLocation) {
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                Location current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                this.storeWorkoutDetails(System.currentTimeMillis(), current.getLatitude(), current.getLongitude(), steps);

            } else {
                this.storeWorkoutDetails(count, random.nextDouble(), random.nextDouble(), steps);
            }
        }
        //Toast.makeText(context, "Count is " + count + " and WorkoutID is " + workoutID, Toast.LENGTH_LONG).show();
        Log.d("ServiceTest", "Count is " + count + " and WorkoutID is " + workoutID);
    }
    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        Log.d("ServiceTest", "Service stopped");
    }

    @Override
    public void onStart(Intent intent, int startid) {
        //Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
        Log.d("ServiceTest", "Service started by user.");
    }

    public void startWorkout(Integer wID){
        workoutID = wID;
        count = 0;
        startRunning();
    }

    public void stopWorkout(){
        this.readAllWorkoutsDetails();
        workoutID = -1;
    }

    public Integer getWorkoutID(){
        return workoutID;
    }


    public void storeWorkoutDetails(long time, double lat, double longt, Integer steps){
        ContentValues values = new ContentValues();
        Log.d("ServiceTest", "WorkoutID is " + workoutID +
                " Time is " + time +
                " Latitude is " + lat +
                " Longitude is " + longt +
                " Steps is " + steps);

        values.put(DataProvider.KEY_DETAIL_ID, workoutID);
        values.put(DataProvider.KEY_DETAIL_TIME, time);
        values.put(DataProvider.KEY_DETAIL_LATITUDE, lat);
        values.put(DataProvider.KEY_DETAIL_LONGITUDE, longt);
        values.put(DataProvider.KEY_DETAIL_STEPS, steps);

        Uri details = Uri.parse(DataProvider.DETAILS_URL);
        Uri uri = getContentResolver().insert(details, values);
    }
    public void readAllWorkoutsDetails() {
        Uri details = Uri.parse(DataProvider.DETAILS_URL);
        Cursor cursor = getContentResolver().query(details, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d("ServiceTest", "ID is " + cursor.getInt(0) + " Time is " + cursor.getInt(1) +
                        " Latitude is " + cursor.getDouble(2) + " Longitude is " + cursor.getDouble(3) +
                        " Step count is " + cursor.getInt(4));

            } while (cursor.moveToNext());

        }
    }

}
