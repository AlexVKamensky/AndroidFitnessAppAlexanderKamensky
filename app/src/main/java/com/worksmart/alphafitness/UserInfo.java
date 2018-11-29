package com.worksmart.alphafitness;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

//import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserInfo extends AppCompatActivity {
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
    }};
    ArrayAdapter<String> genderAdapter;
    AlphaFtinessModel model;
    public static final int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new AlphaFtinessModel(this);
        addUser();
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
                //model.updateProfile();
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

        //populateView();
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
            System.out.println("User Cancelled");
        }
    }
    public void populateView(){
        userName.setText(user.getName());
        userWeight.setText(String.valueOf(user.getWeight()));
        genderSpinner.setPrompt(user.getGender().toString());
       // model.profile.calculateValues(0);
        averageDistance.setText(String.valueOf(model.profile.avgDistance)+ " miles");
        averageCalBurned.setText(String.valueOf(model.profile.avgCalories) + " Cal");
        averageWorkouts.setText(String.valueOf(model.profile.weekWorkoutCount)+" times");
        averageTime.setText(String.valueOf(model.profile.avgTime));
        allTime.setText(String.valueOf(model.profile.totalTime));
        allDistance.setText(String.valueOf(model.profile.totalDistance)+ " miles");
        allWorkouts.setText(String.valueOf(model.profile.totalWorkoutCount)+" times");
        allCalBurned.setText(String.valueOf(model.profile.totalCalories)+ " Cal");

        //profilePic.setImageDrawable(user.getImage());

    }
    public void addUser(){
        UserProfile p = new UserProfile();
        model.getProfile();
        user = model.getProfile();

    }
}
