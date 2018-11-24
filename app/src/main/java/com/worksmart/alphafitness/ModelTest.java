package com.worksmart.alphafitness;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

public class ModelTest {

    static Boolean NewDatabase = false;

    public static void testModel( Activity context){
        AlphaFtinessModel model = new AlphaFtinessModel(context);
        model.getProfile();
        if(model.profile.getName() == ""){
            model.profile = new UserProfile();
            model.profile = new UserProfile();
            model.profile.setName("John Smith");
            model.profile.setGender(UserProfile.Gender.MALE);
            model.addUserProfile();
        }
        Log.d("ModelTest", "The users name is " + model.profile.getName());
        Log.d("ModelTest", "The users gender is " + model.profile.getGender());
        Log.d("ModelTest", "The users weight is " + model.profile.getWeight());
        ArrayList<Workout> workouts = model.getWorkouts();
        if(workouts.isEmpty()) {
            Workout testWorkout = new Workout(1);
            testWorkout.setDistance(1.2);
            testWorkout.setTotalCalories(300);
            testWorkout.setTime(12);
            Log.d("ModelTest", "The distance of the workout is " + testWorkout.getDistance());
            Log.d("ModelTest", "The total calories of the workout is " + testWorkout.getTotalCalories());
            Log.d("ModelTest", "The time of the workout is " + testWorkout.getTime());
            Log.d("ModelTest", "The week is " + testWorkout.getWeek());

            model.profile.workouts.add(testWorkout);

            for (int i = 0; i < 4; i++) {
                Workout newWorkout = new Workout(1);
                newWorkout.setDistance(1.0);
                newWorkout.setTotalCalories(200);
                newWorkout.setTime(10);
                model.profile.workouts.add(newWorkout);
            }

            testWorkout = new Workout(0);
            testWorkout.setTime(30);
            testWorkout.setTotalCalories(500);
            testWorkout.setDistance(12);

            model.profile.workouts.add(testWorkout);
            model.profile.printWorkouts("ModelTest2");
            model.profile.calculateValues(1);

            Log.d("ModelTest", "The number of workouts this week is " + model.profile.weekWorkoutCount);
            Log.d("ModelTest", "The average distance this week is " + model.profile.avgDistance);
            Log.d("ModelTest", "The average time this week is " + model.profile.avgTime);
            Log.d("ModelTest", "The average calories this week is " + model.profile.avgCalories);


            Log.d("ModelTest", "The total number of workouts is " + model.profile.totalWorkoutCount);
            Log.d("ModelTest", "The total distance is " + model.profile.totalDistance);
            Log.d("ModelTest", "The total time is " + model.profile.totalTime);
            Log.d("ModelTest", "The total Calorie count is " + model.profile.totalCalories);

            for (Workout workout : model.profile.workouts) {
                model.addWorkout(workout);
            }

            model.profile.printWorkouts("ModelTest3");

        }
        else{
            model.profile.workouts = workouts;
            model.profile.printWorkouts("ModelTestDB");
        }

        //Workout dbWorkout = database.getWorkOut(0);

    }
}
