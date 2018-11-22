package com.worksmart.alphafitness;

public class Workout {
    private Integer id;
    private Integer totalCalories;
    private Integer time;
    private double distance;
    private Integer week;

    public static Integer idCount=0;


    public Workout(Integer week){
        this.id = idCount;
        idCount = idCount +1;
        this.week  = week;
    }

    public Workout(Integer id, Integer week, Integer totalCalories, Integer time, double distance){
        this.id = id;
        this.week = week;
        this.totalCalories = totalCalories;
        this.time = time;
        this.distance = distance;
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

    public Integer getId() {
        return id;
    }
}
