package com.worksmart.alphafitness;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserInfo extends AppCompatActivity {
    TextView userName, averageDistance, averageTime, averageWorkouts,
            averageCalBurned, allTime, allDistance, allWorkouts, allCalBurned;
    Spinner genderSpinner;
    EditText userWeight;
    ImageView profilePic;
    UserProfile user;
    ArrayList<String> genders = new ArrayList<String>(){{
        add("Male");
        add("Female");
        add("Other");
    }};
    ArrayAdapter<String> genderAdapter;
    AlphaFtinessModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new AlphaFtinessModel(this);
        addUser();
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("ALPHA FITNESS");
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
        populateView();
        //populateView();
    }

    public void populateView(){
        userName.setText(user.getName());
        userWeight.setText(String.valueOf(user.getWeight()));
        genderSpinner.setPrompt(user.getGender().toString());
       // profilePic.setImageDrawable(user.getImage());

    }
    public void addUser(){
        UserProfile p = new UserProfile();
        model.getProfile();
        user = model.getProfile();

    }
}
