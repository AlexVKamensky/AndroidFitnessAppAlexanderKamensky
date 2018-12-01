package com.worksmart.alphafitness;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;

public class UserProfile {


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

    public UserProfile(){
        this.name = "";
        this.gender = "";
        this.weight = 0;
        this.workouts = new ArrayList<Workout>();
    }

    public void loadProfileFromDatabase(){
        // TBD
        return;
    }

    public void saveProfileToDatabase(){
        // TBD
        return;
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

    public void calculateValues(Integer week){
        Integer sumWorkouts = 0;
        double sumDistance = 0;
        Integer sumTime = 0;
        Integer sumCalories = 0;

        this.totalWorkoutCount = 0;
        this.totalDistance = 0;
        this.totalTime = 0;
        this.totalCalories = 0;

        for(Workout workout: this.workouts){
            if(workout.getWeek() == week) {
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
        this.avgDistance = sumDistance/sumWorkouts;
        this.avgTime = sumTime/sumWorkouts;
        this.avgCalories = sumCalories/sumWorkouts;
    }

    public void printWorkouts(String tag){
        for(Workout workout : this.workouts){
            Log.d(tag, "Workout id is " + workout.getId());
            Log.d(tag, "Workout week is " + workout.getWeek());
            Log.d(tag, "Workout time is " + workout.getTime());
            Log.d(tag, "Workout calories are " + workout.getTotalCalories());
            Log.d(tag, "Workout distance is " + workout.getDistance());
        }
    }
}
