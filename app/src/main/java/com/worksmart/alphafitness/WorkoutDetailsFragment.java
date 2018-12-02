package com.worksmart.alphafitness;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class WorkoutDetailsFragment extends Fragment {

    final static private String logId = "WorkoutDetailsFragment";

    // use this factors to make the lines on diffirent scales
    final static private float stepsScaleFactor = 1.1f;
    final static private float caloriesScaleFactor = 1.6f;

    AlphaFtinessModel model;

    TextView instantSpeedText;
    TextView averageSpeedText;
    TextView maxSpeedText;
    TextView minSpeedText;
    LineChart chart;

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
        chart = (LineChart) view.findViewById(R.id.chart);
        chart.setDescription("Step/Calories Chart");

        return view;
    }

    public void updateDetailsUI(){
        //Log.d(logId, "updateDetailsUI called");
        if(AppState.state.workout != null) {
            AlphaFtinessModel.WorkoutDetails details = AlphaFtinessModel.model.getWorkoutDetails(AppState.state.workout.getId());
            AlphaFtinessModel.WorkoutGraphData graphData = model.getGraphData(details);
           // for(String i : graphData.xAxis){
            //    Log.d(logId, "The x axis is "+ i);
            //}
            //for(Entry i : graphData.stepsDataSet){
            //    Log.d(logId, "The value is " + i.getVal() + " the x value is " + i.getXIndex());
            //}
            updateChart(graphData);
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

    private void updateChart(AlphaFtinessModel.WorkoutGraphData data){
        // must clear chart at the start or else it will not update
        chart.clear();
        //chart.setDrawGridBackground(false);

        ArrayList<LineDataSet> lines = new ArrayList<LineDataSet> ();
        if(data.stepsDataSet.size() >= 2) {
            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setAxisMaxValue((float) data.maxSteps * stepsScaleFactor);
            leftAxis.setDrawGridLines(false);
            leftAxis.setTextColor(Color.RED);

            YAxis rightAxis = chart.getAxisRight();
            Log.d(logId, "Max Calories is " + data.maxCalories);
            rightAxis.setAxisMaxValue((float) data.maxCalories * caloriesScaleFactor);
            rightAxis.setDrawGridLines(false);
            rightAxis.setTextColor(Color.BLUE);

            LineDataSet stepsDataSet = new LineDataSet(data.stepsDataSet, "Steps");
            stepsDataSet.setColor(Color.RED);
            stepsDataSet.setCircleColor(Color.RED);
            stepsDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lines.add(stepsDataSet);

            LineDataSet calorieDataSet = new LineDataSet(data.caloriesDataSet, "Calories");
            calorieDataSet.setColor(Color.BLUE);
            calorieDataSet.setCircleColor(Color.BLUE);
            calorieDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            lines.add(calorieDataSet);
            chart.setData(new LineData(data.xAxis, lines));
        }
    }
}
