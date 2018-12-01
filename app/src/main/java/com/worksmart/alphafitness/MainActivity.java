package com.worksmart.alphafitness;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.os.Handler;



public class MainActivity extends AppCompatActivity implements LocationListener {

    private final Integer uiUpdateIntervalMS = 1000;

    RecordWorkoutFragment recordWorkoutFragment;

    public Handler handler = null;
    public static Runnable runnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, WorkoutService.class));
        Log.d("Main", "Calling on create");
        AlphaFtinessModel.model = new AlphaFtinessModel(this);
        //ContentProvider dataProvider = getContentResolver();
        //ModelTest.testModel(this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, WorkoutService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        recordWorkoutFragment = (RecordWorkoutFragment) getSupportFragmentManager()
                .findFragmentById(R.id.RecordWorkoutFragment);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        AppState.state.serviceBound= false;
    }


    public void readWorkoutDetails(Integer id) {
        Uri details = Uri.parse(DataProvider.DETAILS_URL +"/" + id.toString());
        Cursor cursor = getContentResolver().query(details, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d("MainActivityTest", "ID is " + cursor.getInt(0) + " Time is " + cursor.getInt(1) +
                        " Latitude is " + cursor.getDouble(2) + " Longitude is " + cursor.getDouble(3) +
                        " Step Count is " + cursor.getDouble(4));

            } while (cursor.moveToNext());
        }
    }

    public void workoutButtonPressed(View v){
        if(AppState.state.serviceBound){
            if(AppState.state.workout==null){
                AppState.state.workout = new Workout();
                AlphaFtinessModel.model.addWorkout(AppState.state.workout);
                AppState.state.service.startWorkout(AppState.state.workout.getId());
                startWorkoutRecordUiUpdate();
            }
            else {
                AppState.state.service.stopWorkout();
                // we retrieve current workout details one last time in order to update
                // cumulative values in workout
                AlphaFtinessModel.model.getWorkoutDetails(AppState.state.workout.getId());
                // now workout is saved in database
                AlphaFtinessModel.model.updateWorkout(AppState.state.workout);
                //readWorkoutDetails(AppState.state.workout.getId());
                AppState.state.workout = null;
            }
            if(recordWorkoutFragment != null) {
                recordWorkoutFragment.setButtonLabel();
            }
        }
    }

    public void onUserPressed(View v){
        UserProfile user1 = new UserProfile();
        user1.setGender("Male");
        user1.setName("Praneet Singh");
        user1.setWeight(180);
        Intent intent = new Intent( getApplicationContext(), UserInfo.class);
        startActivity(intent);
    }

    public void startWorkoutRecordUiUpdate(){
        handler = new Handler();
        recordWorkoutFragment.startUpdateDetailsUI();
        runnable = new Runnable() {
            public void run() {
                recordWorkoutFragment.updateDetailsUI();
                if (AppState.state.workout != null) {
                    handler.postDelayed(runnable, uiUpdateIntervalMS);
                }
            }
        };

        recordWorkoutFragment.updateDetailsUI();
        handler.postDelayed(runnable, uiUpdateIntervalMS);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder serviceBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            WorkoutService.LocalBinder binder = (WorkoutService.LocalBinder) serviceBinder;
            AppState.state.service = binder.getService();
            AppState.state.serviceBound = true;
            Integer workoutID = AppState.state.service.getWorkoutID();

            if(workoutID != -1){
                AppState.state.workout = AlphaFtinessModel.model.profile.workouts.get(workoutID);
            }
            if(recordWorkoutFragment != null) {
                recordWorkoutFragment.setButtonLabel();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            AppState.state.serviceBound = false;
        }
    };

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
}
