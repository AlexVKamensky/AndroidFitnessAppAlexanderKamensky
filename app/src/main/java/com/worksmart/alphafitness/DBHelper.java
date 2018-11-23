package com.worksmart.alphafitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AlphaFitnessDatabase";
    private static final String DATABASE_TABLE1 = "userProfile_Table";
    private static final String DATABASE_TABLE2 = "workouts_Table";

    private static final String KEY_USERPROFILE_ID = "_id";
    private static final String KEY_USERPROFILE_NAME = "name";
    private static final String KEY_USERPROFILE_GENDER = "gender";
    private static final String KEY_USERPROFILE_WEIGHT = "weight";

    private static final String KEY_WORKOUT_ID = "_id";
    private static final String KEY_WORKOUT_WEEK = "week";
    private static final String KEY_WORKOUT_CALORIES = "calories";
    private static final String KEY_WORKOUT_TIME = "time";
    private static final String KEY_WORKOUT_DISTANCE = "distance";



    public DBHelper (Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String tableUserProfile = "CREATE TABLE " + DATABASE_TABLE1 + "("
                + KEY_USERPROFILE_ID + " INTEGER PRIMARY KEY, "
                + KEY_USERPROFILE_NAME + " TEXT, "
                + KEY_USERPROFILE_GENDER + " TEXT,"
                + KEY_USERPROFILE_WEIGHT + " INTEGER" + ")"
                ;
        database.execSQL (tableUserProfile);

        String tableWorkout = "CREATE TABLE " + DATABASE_TABLE2 + "("
                + KEY_WORKOUT_ID + " INTEGER PRIMARY KEY,"
                + KEY_WORKOUT_WEEK + " INTEGER,"
                + KEY_WORKOUT_TIME + " INTEGER, "
                + KEY_WORKOUT_CALORIES + " INTEGER,"
                + KEY_WORKOUT_DISTANCE + " REAL" + ")"
                ;
        database.execSQL (tableWorkout);


    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE2);
        onCreate(database);
    }

    public void clearDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE2);
        onCreate(db);
    }

    public void addWorkOut(Workout workout){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WORKOUT_ID, workout.getId());
        values.put(KEY_WORKOUT_WEEK, workout.getWeek());
        values.put(KEY_WORKOUT_TIME, workout.getTime());
        values.put(KEY_WORKOUT_CALORIES, workout.getTotalCalories());
        values.put(KEY_WORKOUT_DISTANCE, workout.getDistance());

        db.insert(DATABASE_TABLE2, null, values);
        db.close();

    }

    public void addUserProfile(UserProfile profile){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERPROFILE_ID, 0);
        values.put(KEY_USERPROFILE_NAME, profile.getName());
        values.put(KEY_USERPROFILE_GENDER, profile.getGender().toString());
        values.put(KEY_USERPROFILE_WEIGHT, profile.getWeight());

        db.insert(DATABASE_TABLE1, null, values);
        db.close();
    }

    public ArrayList<Workout> getWorkOuts(){
        ArrayList<Workout> workouts = new ArrayList<Workout>();
        Integer maxId = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                DATABASE_TABLE2,
                new String[]{KEY_WORKOUT_ID,  KEY_WORKOUT_WEEK, KEY_WORKOUT_TIME, KEY_WORKOUT_CALORIES, KEY_WORKOUT_DISTANCE}, null,
                null, null, null, null );

        if (cursor.moveToFirst()) {
            do{
                Integer currId = cursor.getInt(0);
                Workout workout = new Workout(currId, cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getDouble(4));
                workouts.add(workout);
                if(currId > maxId){
                    maxId = currId;
                }

            } while (cursor.moveToNext());

            Workout.idCount = maxId;
        }
        cursor.close();
        db.close();
        return workouts;
    }

    public UserProfile getUserProfile(){
        SQLiteDatabase db = this.getReadableDatabase();
        UserProfile ret = null;
        Cursor cursor = db.query(
                DATABASE_TABLE1,
                new String[]{  KEY_USERPROFILE_NAME, KEY_USERPROFILE_GENDER, KEY_USERPROFILE_WEIGHT, }, null,
                null, null, null, null );

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            ret = new UserProfile();
            ret.setName(cursor.getString(0));
            ret.setGender(cursor.getString(1));
            ret.setWeight(cursor.getInt(2));
        }
        cursor.close();
        db.close();
        return ret;
    }
}
