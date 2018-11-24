package com.worksmart.alphafitness;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class AlphaFtinessModel {
    public UserProfile profile;

    public static AlphaFtinessModel model;
    ContentResolver contentResolver;

    public AlphaFtinessModel(Activity context){
        this.profile = new UserProfile();
        this.contentResolver = context.getContentResolver();
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

            Workout.idCount = maxId;
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
}
