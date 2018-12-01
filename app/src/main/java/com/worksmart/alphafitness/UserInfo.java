package com.worksmart.alphafitness;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UserInfo extends AppCompatActivity {
    final static String logId = "UserInfo";
    private final Integer uiUpdateIntervalMS = 1000;

    public Handler handlerUserInfo = null;
    public static Runnable runnableUserInfo = null;
    TextView averageDistance, averageTime, averageWorkouts,
            averageCalBurned, allTime, allDistance, allWorkouts, allCalBurned;
    Spinner genderSpinner;
    EditText userName,userWeight;
    Button saveButton;
    ImageView profilePic;
    UserProfile user;
    ArrayList<String> genders = new ArrayList<String>(){{
        add("Male");
        add("Female");
        add("Other");
        add("Unspecified");
    }};
    ArrayAdapter<String> genderAdapter;
    AlphaFtinessModel model;
    public static final int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = AlphaFtinessModel.model;
        user = model.profile;

        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("ALPHA FITNESS");
        saveButton = findViewById(R.id.saveButton);
        genderSpinner = findViewById(R.id.genderSpinner);
        userName = findViewById(R.id.userName);
        averageDistance = findViewById(R.id.averageDistance);
        averageTime = findViewById(R.id.averageTime);
        userWeight = findViewById(R.id.weightEditText);
        averageWorkouts = findViewById(R.id.averageWorkouts);
        averageCalBurned = findViewById(R.id.averageCalBurned);
        allTime = findViewById(R.id.allTime);
        allDistance = findViewById(R.id.allDistance);
        allWorkouts = findViewById(R.id.allWorkouts);
        allCalBurned = findViewById(R.id.allCalBurned);

        genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item , genders);
        genderSpinner.setAdapter(genderAdapter);
        profilePic = findViewById(R.id.profilePicture);
        populateView();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.profile.setGender(genderSpinner.getSelectedItem().toString());
                model.profile.setName(userName.getText().toString());
                model.profile.setWeight(Integer.parseInt(userWeight.getText().toString()));
                model.profile.setImage(profilePic.getDrawable());
                Log.d(logId, "The users name is " + model.profile.getName());
                Log.d(logId, "The users gender is " + model.profile.getGender());
                Log.d(logId, "The users weight is " + model.profile.getWeight());
                model.updateProfile();
            }

        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(UserInfo.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(UserInfo.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions(UserInfo.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PICK_IMAGE);

                    }
                } else {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE);

                }
            }
        });
        startUserInfoUpdate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PICK_IMAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE);

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        if (requestCode == 1 && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            profilePic.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }else{
        }
    }
    public void populateView(){
        userName.setText(user.getName());
        userWeight.setText(String.valueOf(user.getWeight()));
        genderSpinner.setSelection(this.genderStringToSpinnerSelection(user.getGender()));
        model.profile.calculateValues();
        String avDis = distanceFormat(model.profile.avgDistance)+ " km";
        averageDistance.setText(avDis);
        String avCal = String.valueOf(model.profile.avgCalories) + " Cal";
        averageCalBurned.setText(avCal);
        String avW = String.valueOf(model.profile.weekWorkoutCount)+" times";
        averageWorkouts.setText(avW);

        averageTime.setText(timeFormat(model.profile.avgTime));

        allTime.setText(timeFormat(model.profile.totalTime));
        String tDis = distanceFormat(model.profile.totalDistance)+ " km";
        allDistance.setText(tDis);
        String tW = String.valueOf(model.profile.totalWorkoutCount)+" times";
        allWorkouts.setText(tW);
        String tCal = String.valueOf(model.profile.totalCalories)+ " Cal";
        allCalBurned.setText(tCal);
        if(user.getImage() != null){
            profilePic.setImageDrawable(user.getImage());
        }
    }

    public static String timeFormat(int timeInMilliSeconds){
        long seconds = timeInMilliSeconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        String time = days + " day " + hours % 24 + " hr " + minutes % 60 + " min " + seconds % 60 + " sec";
        return time;
    }
    public static String distanceFormat(double number) {
        DecimalFormat df = new DecimalFormat("0.000");
        return df.format(number).replaceAll("\\.000$", "");
    }
    public boolean startUpdateDetailsUI(){
        try{
            AlphaFtinessModel.WorkoutDetails details = AlphaFtinessModel.model.getWorkoutDetails(AppState.state.workout.getId());
            return true;
        }catch (Exception e){
            return false;
        }
    }
    private int genderStringToSpinnerSelection(String gender){
        int ret = 3;

        if(gender.equals("Male")){
            ret=0;
        }
        else if(gender.equals("Female")){
            ret = 1;
        }
        else if(gender.equals("Other")){
            ret = 2;
        }
        return ret;
    }
    public String timeFormatAvg(long timeInMilliSeconds){
        timeInMilliSeconds = (model.profile.avgTime+timeInMilliSeconds)/2;

        long seconds = timeInMilliSeconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        String time = days + " day " + hours % 24 + " hr " + minutes % 60 + " min " + seconds % 60 + " sec";
        return time;
    }
    public String timeFormatAll(long timeInMilliSeconds){
        timeInMilliSeconds += model.profile.totalTime;
        long seconds = timeInMilliSeconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        String time = days + " day " + hours % 24 + " hr " + minutes % 60 + " min " + seconds % 60 + " sec";
        return time;
    }
    public void updateDetailsUI(){
        //Log.d(logId, "updateDetailsUI called");
        if(AppState.state.workout != null) {
            AlphaFtinessModel.WorkoutDetails details = AlphaFtinessModel.model.getWorkoutDetails(AppState.state.workout.getId());
            averageDistance.setText(distanceFormat((details.distance+model.profile.avgDistance)/2));
            averageTime.setText(timeFormatAvg(details.duration));
            //averageCalBurned.setText();
            allDistance.setText(distanceFormat(details.distance+model.profile.totalDistance));
            allTime.setText(timeFormatAll(details.duration));
        }
    }
    public void startUserInfoUpdate(){
        handlerUserInfo = new Handler();
        if(startUpdateDetailsUI()) {
            runnableUserInfo = new Runnable() {
                public void run() {
                    updateDetailsUI();
                    if (AppState.state.workout != null) {
                        handlerUserInfo.postDelayed(runnableUserInfo, uiUpdateIntervalMS);
                    }
                }
            };

            updateDetailsUI();
            handlerUserInfo.postDelayed(runnableUserInfo, uiUpdateIntervalMS);
        }

    }

}
