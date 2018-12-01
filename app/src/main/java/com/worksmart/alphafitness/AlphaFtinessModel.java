package com.worksmart.alphafitness;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class AlphaFtinessModel {
    public UserProfile profile;

    public static AlphaFtinessModel model;
    ContentResolver contentResolver;

    public Workout currWorkout;


    public AlphaFtinessModel(Activity context){
        this.profile = new UserProfile();
        this.contentResolver = context.getContentResolver();
        model = this;
    }


    public void addWorkout(Workout workout){
        //this.profile.workouts.add(workout);
        ContentValues values = new ContentValues();
        values.put(DataProvider.KEY_WORKOUT_ID, workout.getId());
        values.put(DataProvider.KEY_WORKOUT_TIME, workout.getTime());
        values.put(DataProvider.KEY_WORKOUT_WEEK, workout.getWeek());
        values.put(DataProvider.KEY_WORKOUT_CALORIES, workout.getTotalCalories());
        values.put(DataProvider.KEY_WORKOUT_DISTANCE, workout.getDistance());

        Uri workouts = Uri.parse(DataProvider.WORKOUT_URL);
        Uri uri = this.contentResolver.insert(workouts, values);
    }

    public void addUserProfile(){
        ContentValues values = new ContentValues();
        values.put(DataProvider.KEY_USERPROFILE_ID, 1);
        values.put(DataProvider.KEY_USERPROFILE_NAME, this.profile.getName());
        values.put(DataProvider.KEY_USERPROFILE_GENDER, this.profile.getGender().toString());
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
                Workout workout = new Workout(currId, cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getDouble(4));
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
        Uri uprofile = Uri.parse(DataProvider.USERPROFILE_URL);
        Cursor cursor = this.contentResolver.query(uprofile, null, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            this.profile.setName(cursor.getString(0));
            this.profile.setGender(cursor.getString(1));
            this.profile.setWeight(cursor.getInt(2));
        }
        cursor.close();
        return this.profile;
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
                WorkoutSample sample = new WorkoutSample(coord, cursor.getDouble(4), cursor.getInt(1));
                wd.basicdata.add(sample);

            } while (cursor.moveToNext());
        }
        float[] results = new float[1];
        float distance = 0;
        WorkoutSample prev = null;
        for (WorkoutSample sample: wd.basicdata){
            if(prev != null){
                Location.distanceBetween(prev.coordinate.latitude, prev.coordinate.longitude,
                            sample.coordinate.latitude, sample.coordinate.longitude,
                            results);
                distance = distance + results[0];
            }
            prev = sample;
        }
        wd.distance = distance/1000.0;
        wd.duration = wd.basicdata.get(wd.basicdata.size()-1).time - wd.basicdata.get(0).time;
        return wd;
    }


    public static class WorkoutSample{
        LatLng coordinate;
        double steps;
        long time;

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
