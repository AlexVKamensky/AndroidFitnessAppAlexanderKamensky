package com.worksmart.alphafitness;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    WorkoutService service;
    boolean bound = false;

    boolean newWorkout = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, WorkoutService.class));
        Log.d("Main", "Calling on create");
        //ContentProvider dataProvider = getContentResolver();
        ModelTest.testModel(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, WorkoutService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        bound = false;
    }

    public void workoutButtonPressed(View v){
        if(bound){
            if(newWorkout){
                Workout workout = new Workout(1);
                service.startWorkout(workout.getId());
                newWorkout = false;
            }
            else {
                service.stopWorkout();
                newWorkout = true;
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder serviceBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            WorkoutService.LocalBinder binder = (WorkoutService.LocalBinder) serviceBinder;
            service = binder.getService();
            bound = true;
            if(service.getWorkoutID() != -1){
                newWorkout = false;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

}
