package com.worksmart.alphafitness;
import android.util.Log;

import java.util.ArrayList;

public class ModelTest {

    static Boolean NewDatabase = false;

    public static void testModel(DBHelper database){
        if(NewDatabase == true){
            database.clearDB();
        }
        UserProfile testProfile = database.getUserProfile();
        if(testProfile == null){
            testProfile = new UserProfile();
            testProfile.setName("John Smith");
            testProfile.setGender(UserProfile.Gender.MALE);
            database.addUserProfile(testProfile);
        }
        Log.d("ModelTest", "The users name is " + testProfile.getName());
        Log.d("ModelTest", "The users gender is " + testProfile.getGender());
        Log.d("ModelTest", "The users weight is " + testProfile.getWeight());
        ArrayList<Workout> workouts = database.getWorkOuts();
        if(workouts.isEmpty()) {
            Workout testWorkout = new Workout(1);
            testWorkout.setDistance(1.2);
            testWorkout.setTotalCalories(300);
            testWorkout.setTime(12);
            Log.d("ModelTest", "The distance of the workout is " + testWorkout.getDistance());
            Log.d("ModelTest", "The total calories of the workout is " + testWorkout.getTotalCalories());
            Log.d("ModelTest", "The time of the workout is " + testWorkout.getTime());
            Log.d("ModelTest", "The week is " + testWorkout.getWeek());

            testProfile.workouts.add(testWorkout);

            for (int i = 0; i < 4; i++) {
                Workout newWorkout = new Workout(1);
                newWorkout.setDistance(1.0);
                newWorkout.setTotalCalories(200);
                newWorkout.setTime(10);
                testProfile.workouts.add(newWorkout);
            }

            testWorkout = new Workout(0);
            testWorkout.setTime(30);
            testWorkout.setTotalCalories(500);
            testWorkout.setDistance(12);

            testProfile.workouts.add(testWorkout);
            testProfile.printWorkouts("ModelTest2");
            testProfile.calculateValues(1);

            Log.d("ModelTest", "The number of workouts this week is " + testProfile.weekWorkoutCount);
            Log.d("ModelTest", "The average distance this week is " + testProfile.avgDistance);
            Log.d("ModelTest", "The average time this week is " + testProfile.avgTime);
            Log.d("ModelTest", "The average calories this week is " + testProfile.avgCalories);


            Log.d("ModelTest", "The total number of workouts is " + testProfile.totalWorkoutCount);
            Log.d("ModelTest", "The total distance is " + testProfile.totalDistance);
            Log.d("ModelTest", "The total time is " + testProfile.totalTime);
            Log.d("ModelTest", "The total Calorie count is " + testProfile.totalCalories);

            Log.d("ModelTest", "Database is " + database.toString());
            for (Workout workout : testProfile.workouts) {
                database.addWorkOut(workout);
            }

            testProfile.printWorkouts("ModetTest3");
        }
        else{
            testProfile.workouts = workouts;
            testProfile.printWorkouts("ModelTest3");
        }

        //Workout dbWorkout = database.getWorkOut(0);

    }
}
