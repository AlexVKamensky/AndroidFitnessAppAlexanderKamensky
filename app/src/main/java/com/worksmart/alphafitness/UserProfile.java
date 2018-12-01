package com.worksmart.alphafitness;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;

public class UserProfile {

    final static private String logId = "UserProfile";

    // persistent state should be saved/read into/from database
    private String name;
    private Drawable image;
    private String gender;
    private Integer weight;
    public ArrayList<Workout> workouts;

    /* transient states for current week
     * derived from workouts of this week
     * will be filled by calculateValues method
     */
    public double avgDistance;
    public Integer avgTime;
    public Integer weekWorkoutCount;
    public Integer avgCalories;

    /* transient states for overall
     * derived from all workouts
     * will be filled by calculateValues method
     */
    double totalDistance;
    Integer totalTime;
    Integer totalWorkoutCount;
    Integer totalCalories;

    double caloriesPerThousandSteps;

    public UserProfile(){
        this.name = "";
        this.gender = "";
        this.weight = 0;
        this.workouts = new ArrayList<Workout>();
    }


    public void setName(String name) {
        this.name = name;
    }



    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setImage(Drawable image){
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public Integer getWeight() {
        return weight;
    }

    public Drawable getImage() {
        return image;
    }

    public void calculateValues(){
        Integer sumWorkouts = 0;
        double sumDistance = 0;
        Integer sumTime = 0;
        Integer sumCalories = 0;

        this.totalWorkoutCount = 0;
        this.totalDistance = 0;
        this.totalTime = 0;
        this.totalCalories = 0;
        long now = System.currentTimeMillis();
        long lastWeekStart = now - (7*24*60*60*1000);
        for(Workout workout: this.workouts){
            Log.d(logId, "lastWeekStart = " + lastWeekStart);
            Log.d(logId, "workout start time = " + workout.getStartTime());
            if(workout.getStartTime() >= lastWeekStart) {
                Log.d(logId, "Last week workout");
                sumWorkouts = sumWorkouts + 1;
                sumDistance = sumDistance + workout.getDistance();
                sumTime = sumTime + workout.getTime();
                sumCalories = sumCalories + workout.getTotalCalories();
            }
            this.totalWorkoutCount = this.totalWorkoutCount +1;
            this.totalDistance = this.totalDistance + workout.getDistance();
            this.totalTime = this.totalTime + workout.getTime();
            this.totalCalories = this.totalCalories + workout.getTotalCalories();
        }
        this.weekWorkoutCount = sumWorkouts;
        if (sumWorkouts != 0) {
            this.avgDistance = sumDistance / sumWorkouts;
            this.avgTime = sumTime / sumWorkouts;
            this.avgCalories = sumCalories / sumWorkouts;
        }
    }

    public double getCaloriesPerThousandSteps(){
        // numbers derived from table for 5'6''-5'1'' at
        // https://www.verywellfit.com/pedometer-steps-to-calories-converter-3882595
        double ret = 25.0 + ((this.weight - 45.0) / 9.0) * 5;
        if(ret < 0){
            ret = 0;
        }
        return ret;
    }

    public void printWorkouts(String tag){
        for(Workout workout : this.workouts){
            Log.d(tag, "Workout id is " + workout.getId());
            Log.d(tag, "Workout start time is " + workout.getStartTime());
            Log.d(tag, "Workout time is " + workout.getTime());
            Log.d(tag, "Workout calories are " + workout.getTotalCalories());
            Log.d(tag, "Workout distance is " + workout.getDistance());
        }
    }
}
