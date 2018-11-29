package com.worksmart.alphafitness;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {

    RecordWorkoutFragment recordWorkoutFragment;

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
                        " Step Count is " + cursor.getInt(4));

            } while (cursor.moveToNext());
        }
    }

    public void workoutButtonPressed(View v){
        if(AppState.state.serviceBound){
            if(AppState.state.workout==null){
                AppState.state.workout = new Workout(1);
                AlphaFtinessModel.model.getProfile().workouts.add(AppState.state.workout);
                AlphaFtinessModel.model.addWorkout(AppState.state.workout);
                AppState.state.service.startWorkout(AppState.state.workout.getId());
            }
            else {
                AppState.state.service.stopWorkout();
                readWorkoutDetails(AppState.state.workout.getId());
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
}
