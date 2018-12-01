package com.worksmart.alphafitness;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class AlphaFtinessModel {
    final private String logId = "AlphaFtinessModel";
    final private Integer speedSampleSize = 5;

    public UserProfile profile;

    public static AlphaFtinessModel model;
    ContentResolver contentResolver;

    public Workout currWorkout;


    public AlphaFtinessModel(Activity context){
        model = this;
        this.contentResolver = context.getContentResolver();
        this.getProfile();
        Log.d(logId, "The users name is " + profile.getName());
        Log.d(logId, "The users gender is " + profile.getGender());
        Log.d(logId, "The users weight is " + profile.getWeight());
        getWorkouts();
        profile.printWorkouts(logId);
    }


    public void addWorkout(Workout workout){
        ContentValues values = new ContentValues();
        values.put(DataProvider.KEY_WORKOUT_ID, workout.getId());
        values.put(DataProvider.KEY_WORKOUT_TIME, workout.getTime());
        values.put(DataProvider.KEY_WORKOUT_START_TIME, workout.getStartTime());
        values.put(DataProvider.KEY_WORKOUT_CALORIES, workout.getTotalCalories());
        values.put(DataProvider.KEY_WORKOUT_DISTANCE, workout.getDistance());

        Uri workouts = Uri.parse(DataProvider.WORKOUT_URL);
        Uri uri = this.contentResolver.insert(workouts, values);
    }


    public void addUserProfile(){
        ContentValues values = new ContentValues();
        values.put(DataProvider.KEY_USERPROFILE_ID, 1);
        values.put(DataProvider.KEY_USERPROFILE_NAME, this.profile.getName());
        values.put(DataProvider.KEY_USERPROFILE_GENDER, this.profile.getGender());
        values.put(DataProvider.KEY_USERPROFILE_WEIGHT, this.profile.getWeight());

        Uri uprofile = Uri.parse(DataProvider.USERPROFILE_URL);
        Uri uri = this.contentResolver.insert(uprofile, values);
    }

    public ArrayList<Workout> getWorkouts() {
        ArrayList<Workout> workoutlist = new ArrayList<Workout>();
        Integer maxId = 0;
        Uri workouts = Uri.parse(DataProvider.WORKOUT_URL);
        Cursor cursor = this.contentResolver.query(workouts, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Integer currId = cursor.getInt(0);
                Workout workout = new Workout(currId, cursor.getLong(1), cursor.getInt(2), cursor.getInt(3), cursor.getDouble(4));
                workoutlist.add(workout);
                if (currId > maxId) {
                    maxId = currId;
                }

            } while (cursor.moveToNext());

            Workout.idCount = maxId+1;
        }
        cursor.close();
        return workoutlist;
    }

    public UserProfile getProfile(){
        this.profile = new UserProfile();
        Uri uprofile = Uri.parse(DataProvider.USERPROFILE_URL);
        Cursor cursor = this.contentResolver.query(uprofile, null, null, null, null);
        if (cursor.moveToFirst()) {
            //cursor.moveToFirst();
            this.profile.setName(cursor.getString(1));
            this.profile.setGender(cursor.getString(2));
            this.profile.setWeight(cursor.getInt(3));
        }else {
            // first time no user profile in database create generic one
            profile.setName("Enter your Name");
            profile.setGender("Other");
            profile.setWeight(0);
            this.addUserProfile();
        }
        cursor.close();
        return this.profile;
    }

    public void updateProfile(){
        Uri uprofile = Uri.parse(DataProvider.USERPROFILE_URL);
        ContentValues values = new ContentValues();
        values.put(DataProvider.KEY_USERPROFILE_ID, 1);
        values.put(DataProvider.KEY_USERPROFILE_NAME, this.profile.getName());
        values.put(DataProvider.KEY_USERPROFILE_GENDER, this.profile.getGender());
        values.put(DataProvider.KEY_USERPROFILE_WEIGHT, this.profile.getWeight());
        String mSelectionClause = DataProvider.KEY_USERPROFILE_ID + " = 1";
        this.contentResolver.update(uprofile, values, mSelectionClause, null);
    }

    public void updateWorkout(Workout workout){
        ContentValues values = new ContentValues();
        values.put(DataProvider.KEY_WORKOUT_ID, workout.getId());
        values.put(DataProvider.KEY_WORKOUT_TIME, workout.getTime());
        values.put(DataProvider.KEY_WORKOUT_START_TIME, workout.getStartTime());
        values.put(DataProvider.KEY_WORKOUT_CALORIES, workout.getTotalCalories());
        values.put(DataProvider.KEY_WORKOUT_DISTANCE, workout.getDistance());
        String mSelectionClause = DataProvider.KEY_WORKOUT_ID + " = " + workout.getId();
        Uri workouts = Uri.parse(DataProvider.WORKOUT_URL);
        this.contentResolver.update(workouts, values, mSelectionClause, null);
    }

    public WorkoutDetails getWorkoutDetails(Integer id){
        WorkoutDetails wd = new WorkoutDetails();
        Uri details = Uri.parse(DataProvider.DETAILS_URL +"/" + id.toString());
        Cursor cursor = this.contentResolver.query(details, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                //Log.d("getWorkoutDetails", "ID is " + cursor.getInt(0) + " Time is " + cursor.getInt(1) +
                //       " Latitude is " + cursor.getDouble(2) + " Longitude is " + cursor.getDouble(3) +
                //        " Step Count is " + cursor.getDouble(4));
                LatLng coord = new LatLng(cursor.getDouble(2), cursor.getDouble(3));
                WorkoutSample sample = new WorkoutSample(coord, cursor.getDouble(4), cursor.getLong(1));
                wd.basicdata.add(sample);

            } while (cursor.moveToNext());
        }
        float[] results = new float[1];
        float distance = 0;
        double steps = 0;
        Integer count = 0;
        WorkoutSample prev = null;
        for (WorkoutSample sample: wd.basicdata){
            if(prev != null){
                Location.distanceBetween(prev.coordinate.latitude, prev.coordinate.longitude,
                            sample.coordinate.latitude, sample.coordinate.longitude,
                            results);
                distance = distance + results[0];
                sample.distanceSincePrevSample = results[0];
                steps = steps + sample.steps;
            }

            double speedDistance = 0;
            long speedStartTime = 0;
            if(count >= speedSampleSize){
                for(int i = 0; i < speedSampleSize; i++){
                    speedDistance = speedDistance + wd.basicdata.get(count - i).distanceSincePrevSample;
                    speedStartTime = wd.basicdata.get(count -i).time;
                }
                long intervalTime = sample.time - speedStartTime;
                // convert to seconds
                intervalTime = intervalTime/1000;
                wd.speed = speedDistance/intervalTime;
                if(wd.speed > wd.maxSpeed){
                    wd.maxSpeed = wd.speed;
                }
                if((wd.minSpeed == 0) || (wd.speed < wd.minSpeed)){
                    wd.minSpeed = wd.speed;
                }
            }

            prev = sample;
            count = count + 1;
        }
        wd.distance = distance/1000.0;
        wd.duration = wd.basicdata.get(wd.basicdata.size()-1).time - wd.basicdata.get(0).time;
        //Log.d(logId, "Speed is " + wd.speed + " Max Speed is " + wd.maxSpeed + " Min Speed is " + wd.minSpeed);
        Integer calories =  getCaloriesFromSteps(steps);
        AppState.state.workout.setStartTime(wd.basicdata.get(0).time);
        AppState.state.workout.setDistance(wd.distance);
        AppState.state.workout.setTime((int) wd.duration);
        AppState.state.workout.setTotalCalories(calories);
        return wd;
    }

    public Integer getCaloriesFromSteps(double steps){
        return new Integer((int) (steps*profile.getCaloriesPerThousandSteps()/1000.0));
    }

    public static class WorkoutSample{
        LatLng coordinate;
        double steps;
        long time;
        double distanceSincePrevSample;

        public WorkoutSample(LatLng coordinate, double steps, long time){
            this.coordinate = coordinate;
            this.steps = steps;
            this.time = time;
        }
    }

    public class WorkoutDetails{

        public ArrayList<WorkoutSample> basicdata;
        public double distance;
        public long duration;
        public double minSpeed;
        public double maxSpeed;
        public double speed;

        public WorkoutDetails(){
            basicdata = new ArrayList<WorkoutSample>();
            distance = 0;
            minSpeed = 0;
            maxSpeed = 0;
            speed = 0;
            duration = 0;
        }
    }
}
