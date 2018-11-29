package com.worksmart.alphafitness;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RecordWorkoutFragment extends Fragment {

    static final String logId = "RecordWorkout";
    AlphaFtinessModel model;
    Button workoutButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_workout, container, false);
        model = AlphaFtinessModel.model;
        workoutButton = (Button) view.findViewById(R.id.workoutbutton);
        this.setButtonLabel();
        return view;
    }

    public void setButtonLabel(){
        //Log.d(logId, "setButtonLabel called");
        if (workoutButton != null) {
            //Log.d(logId, "workoutButton not null");
            if (AppState.state.workout == null) {
                //Log.d(logId, "Workout is null");
                workoutButton.setText(R.string.record_workout_record_button_start_text);
            } else {
                //Log.d(logId, "Workout is not null");
                workoutButton.setText(R.string.record_workout_record_button_end_text);
            }
        }
    }
}
