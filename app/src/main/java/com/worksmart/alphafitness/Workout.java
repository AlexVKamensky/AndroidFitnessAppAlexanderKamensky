package com.worksmart.alphafitness;

public class Workout {
    private Integer totalCalories;
    private Integer time;
    private double distance;
    private Integer week;


    public Workout(Integer week){
        this.week  = week;
    }

    public void setTotalCalories(Integer totalCalories) {
        this.totalCalories = totalCalories;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Integer getTotalCalories() {
        return totalCalories;
    }

    public Integer getTime() {
        return time;
    }

    public double getDistance() {
        return distance;
    }

    public Integer getWeek() {
        return week;
    }
}
