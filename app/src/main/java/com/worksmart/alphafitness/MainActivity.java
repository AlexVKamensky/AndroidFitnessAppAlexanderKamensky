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

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    WorkoutService service;
    boolean bound = false;

    Button workoutButton;

    boolean newWorkout = true;
    Workout workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, WorkoutService.class));
        Log.d("Main", "Calling on create");
        //ContentProvider dataProvider = getContentResolver();
        ModelTest.testModel(this);
        workoutButton = (Button) findViewById(R.id.workoutbutton);
        this.setButtonLabel();
    }

    private void setButtonLabel(){
        if(workout== null) {
            workoutButton.setText("Start Workout");
        }
        else{
            workoutButton.setText("End Workout");
        }
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
            if(workout==null){
                workout = new Workout(1);
                AlphaFtinessModel.model.getProfile().workouts.add(workout);
                AlphaFtinessModel.model.addWorkout(workout);
                service.startWorkout(workout.getId());
            }
            else {
                service.stopWorkout();
                this.readWorkoutDetails(workout.getId());
                workout = null;
            }
            this.setButtonLabel();
        }
    }
    public void oneUserPressed(View v){
        UserProfile user1 = new UserProfile();
        user1.setGender("Male");
        user1.setName("Praneet Singh");
        user1.setWeight(180);
        Intent intent = new Intent( getApplicationContext(), UserInfo.class);
        Gson userGson = new Gson();
        String userString = userGson.toJson(user1);
        intent.putExtra("userInfo", userString);
        startActivity(intent);
    }
    private void readWorkoutDetails(Integer id) {
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
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder serviceBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            WorkoutService.LocalBinder binder = (WorkoutService.LocalBinder) serviceBinder;
            service = binder.getService();
            bound = true;
            Integer workoutID = service.getWorkoutID();

            if(workoutID != -1){
                workout = AlphaFtinessModel.model.profile.workouts.get(workoutID);
                newWorkout = false;
            }
            setButtonLabel();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

}
