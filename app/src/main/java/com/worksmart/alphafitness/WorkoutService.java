package com.worksmart.alphafitness;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WorkoutService extends Service {

    private final Integer sampleIntervalMS = 5000;

    private final IBinder mBinder = new LocalBinder();

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    private Integer count;
    private Integer workoutID;

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

    public void startRunning(){
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                doSample();
                if(workoutID != -1){

                    handler.postDelayed(runnable, sampleIntervalMS);
                }
            }
        };
        doSample();
        handler.postDelayed(runnable, sampleIntervalMS);
    }

    public void doSample(){
        count = count + 1;
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
        workoutID = -1;
    }

    public Integer getWorkoutID(){
        return workoutID;
    }
}
