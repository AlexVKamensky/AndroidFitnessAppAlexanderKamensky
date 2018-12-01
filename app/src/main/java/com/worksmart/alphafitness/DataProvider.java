package com.worksmart.alphafitness;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;


public class DataProvider extends ContentProvider {

    final static String logId = "ContentProvider";

    static final String PROVIDER_NAME = "com.worksmart.alphafitness.DataProvider";
    static final String USERPROFILE_URL = "content://" + PROVIDER_NAME + "/profile";
    static final Uri USERPROFILE_URI = Uri.parse(USERPROFILE_URL);
    static final String WORKOUT_URL = "content://" + PROVIDER_NAME + "/workouts";
    static final Uri WORKOUT_URI = Uri.parse(WORKOUT_URL);
    static final String DETAILS_URL = "content://" + PROVIDER_NAME + "/details";
    static final Uri DETAILS_URI = Uri.parse(DETAILS_URL);

    static final String KEY_USERPROFILE_ID = "_id";
    static final String KEY_USERPROFILE_NAME = "name";
    static final String KEY_USERPROFILE_GENDER = "gender";
    static final String KEY_USERPROFILE_WEIGHT = "weight";

    static final String KEY_WORKOUT_ID = "_id";
    static final String KEY_WORKOUT_START_TIME = "start";
    static final String KEY_WORKOUT_CALORIES = "calories";
    static final String KEY_WORKOUT_TIME = "time";
    static final String KEY_WORKOUT_DISTANCE = "distance";

    static final String KEY_DETAIL_ID = "id";
    static final String KEY_DETAIL_TIME = "time";
    static final String KEY_DETAIL_LATITUDE = "latitude";
    static final String KEY_DETAIL_LONGITUDE = "longitude";
    static final String KEY_DETAIL_STEPS = "steps";

    static final int WORKOUTS = 1;
    static final int WORKOUT_ID = 2;
    static final int USERPROFILE = 3;
    static final int DETAILS = 4;
    static final int DETAILS_ID = 5;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "workouts", WORKOUTS);
        uriMatcher.addURI(PROVIDER_NAME, "workouts/#", WORKOUT_ID);
        uriMatcher.addURI(PROVIDER_NAME, "profile", USERPROFILE);
        uriMatcher.addURI(PROVIDER_NAME,"details", DETAILS);
        uriMatcher.addURI(PROVIDER_NAME, "details/#", DETAILS_ID);
    }

    /*
     * one way, if you need to wipe the Database change the version number
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AlphaFitnessDatabase";
    static final String WORKOUT_TABLE_NAME = "workouts_Table";
    static final String USERPROFILE_TABLE_NAME= "userProfile_Table";
    static final String DETAIL_TABLE_NAME = "detail_Table";

    /*
     * other way, to clean Database is set WIPEDB to true
     */
    private static final Boolean WIPEDB = false;

    private DBHelper dbhelper;
    private SQLiteDatabase db;

    public class DBHelper extends SQLiteOpenHelper {


        public DBHelper (Context context){
            super (context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            String tableUserProfile = "CREATE TABLE " + USERPROFILE_TABLE_NAME + "("
                    + KEY_USERPROFILE_ID + " INTEGER PRIMARY KEY, "
                    + KEY_USERPROFILE_NAME + " TEXT, "
                    + KEY_USERPROFILE_GENDER + " TEXT,"
                    + KEY_USERPROFILE_WEIGHT + " INTEGER" + ")"
                    ;
            database.execSQL (tableUserProfile);

            String tableWorkout = "CREATE TABLE " + WORKOUT_TABLE_NAME + "("
                    + KEY_WORKOUT_ID + " INTEGER PRIMARY KEY,"
                    + KEY_WORKOUT_START_TIME + " INTEGER,"
                    + KEY_WORKOUT_TIME + " INTEGER, "
                    + KEY_WORKOUT_CALORIES + " INTEGER,"
                    + KEY_WORKOUT_DISTANCE + " REAL" + ")"
                    ;
            database.execSQL (tableWorkout);

            String tableDetail = "CREATE TABLE " + DETAIL_TABLE_NAME + "("
                    + KEY_DETAIL_ID + " INTEGER,"
                    + KEY_DETAIL_TIME + " INTEGER,"
                    + KEY_DETAIL_LATITUDE + " REAL,"
                    + KEY_DETAIL_LONGITUDE + " REAL,"
                    + KEY_DETAIL_STEPS + " REAL,"
                    + "PRIMARY KEY (" + KEY_DETAIL_ID + "," + KEY_DETAIL_TIME + " )  )"
                    ;
            database.execSQL (tableDetail);

        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int i, int i1) {
            database.execSQL("DROP TABLE IF EXISTS " + USERPROFILE_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + WORKOUT_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DETAIL_TABLE_NAME);
            onCreate(database);
        }

        public void clearDB(){
            SQLiteDatabase db = this.getWritableDatabase();
            onUpgrade(db, 1, 1);
        }

    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        this.dbhelper = new DBHelper(context);
        if(WIPEDB){
            this.dbhelper.clearDB();
        }
        db = this.dbhelper.getWritableDatabase();
        return (db == null)? false:true;
    }


    @Override
    public Cursor query( Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case WORKOUTS:
                Log.d(logId, "Qeury for workouts");
                qb.setTables(WORKOUT_TABLE_NAME);
                break;

            case WORKOUT_ID:
                qb.setTables(WORKOUT_TABLE_NAME);
                qb.appendWhere( KEY_WORKOUT_ID + "=" + uri.getPathSegments().get(1));
                break;

            case USERPROFILE:
                qb.setTables(USERPROFILE_TABLE_NAME);
                break;
            case DETAILS:
                qb.setTables(DETAIL_TABLE_NAME);
                break;
            case DETAILS_ID:
                qb.setTables(DETAIL_TABLE_NAME);
                qb.appendWhere( KEY_DETAIL_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID;
        switch (uriMatcher.match(uri)) {
            case WORKOUTS:
                Log.d(logId, "Inserting workout");
                rowID = db.insert(WORKOUT_TABLE_NAME, "", values);

                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(WORKOUT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;

            case USERPROFILE:
                rowID = db.insert(USERPROFILE_TABLE_NAME, "", values);

                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(USERPROFILE_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            case DETAILS:
                rowID = db.insert(DETAIL_TABLE_NAME, "", values);

                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(DETAILS_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;
            default:
                throw new SQLException("Failed to add a record into " + uri);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case WORKOUTS:
                count = db.delete(WORKOUT_TABLE_NAME, selection, selectionArgs);
                break;

            case WORKOUT_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( WORKOUT_TABLE_NAME, KEY_WORKOUT_ID +  " = " + id +
                                (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : ""), selectionArgs);
                break;
            case USERPROFILE:
                count = db.delete(USERPROFILE_TABLE_NAME, selection, selectionArgs);
                break;
            case DETAILS:
                count = db.delete(DETAIL_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case WORKOUTS:
                Log.d(logId, "Updating Workouts");
                count = db.update(WORKOUT_TABLE_NAME, values, selection, selectionArgs);
                break;

            case WORKOUT_ID:
                count = db.update(WORKOUT_TABLE_NAME, values,
                        KEY_WORKOUT_ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? "AND (" +selection + ')' : ""), selectionArgs);
                break;
            case USERPROFILE:
                count = db.update(USERPROFILE_TABLE_NAME, values, selection, selectionArgs);
                break;
            case DETAILS:
                count = db.update(DETAIL_TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
