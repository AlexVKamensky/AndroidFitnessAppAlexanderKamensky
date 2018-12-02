package com.worksmart.alphafitness;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

public class WorkoutDetailsFragment extends Fragment {

    final static private String logId = "WorkoutDetailsFragment";

    AlphaFtinessModel model;

    TextView instantSpeedText;
    TextView averageSpeedText;
    TextView maxSpeedText;
    TextView minSpeedText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout_details, container, false);
        model = AlphaFtinessModel.model;

        instantSpeedText = (TextView) view.findViewById(R.id.workoutDetailsInstantSpeedNumber);
        averageSpeedText = (TextView) view.findViewById(R.id.workoutDetailsAverageSpeedNumber);
        maxSpeedText = (TextView) view.findViewById(R.id.workoutDetailsMaxSpeedNumber);
        minSpeedText = (TextView) view.findViewById(R.id.workoutDetailsMinSpeedNumber);

        return view;
    }

    public void updateDetailsUI(){
        //Log.d(logId, "updateDetailsUI called");
        if(AppState.state.workout != null) {
            AlphaFtinessModel.WorkoutDetails details = AlphaFtinessModel.model.getWorkoutDetails(AppState.state.workout.getId());
            instantSpeedText.setText(this.formatSpeed(details.speed));
            averageSpeedText.setText(this.formatSpeed(details.avgSpeed));
            maxSpeedText.setText(this.formatSpeed(details.maxSpeed));
            minSpeedText.setText(this.formatSpeed(details.minSpeed));
        }
    }

    private String formatSpeed(double speed){
        DecimalFormat df = new DecimalFormat("0.000");
        return df.format(speed).replaceAll("\\.000$", "");
    }
}
